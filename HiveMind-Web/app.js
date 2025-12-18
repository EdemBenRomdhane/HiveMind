/**
 * HiveMind Dashboard Logic
 * Real-time data fetching and UI orchestration
 */

const API_BASE = 'http://localhost:8089/api';
const REFRESH_INTERVAL = 3000; // 3 seconds

// State management
let state = {
    events: [],
    alerts: [],
    lastUpdate: null
};

/**
 * Initialization
 */
document.addEventListener('DOMContentLoaded', () => {
    console.log('🚀 HiveMind Interface Initializing...');
    updateTime();
    startPolling();

    // Set up live time clock
    setInterval(updateTime, 1000);
});

function updateTime() {
    const now = new Date();
    document.getElementById('live-time').textContent = now.toLocaleString();
}

/**
 * Data Fetching
 */
async function fetchData() {
    try {
        const [eventsRes, alertsRes] = await Promise.all([
            fetch(`${API_BASE}/events`),
            fetch(`${API_BASE}/alerts`)
        ]);

        if (eventsRes.ok && alertsRes.ok) {
            const events = await eventsRes.json();
            const alerts = await alertsRes.json();

            // Only update if data changed (simple length check)
            if (events.length !== state.events.length || alerts.length !== state.alerts.length) {
                state.events = events.sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp));
                state.alerts = alerts.sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp));
                renderUI();
            }

            document.getElementById('api-health').className = 'badge badge-online';
            document.getElementById('api-health').textContent = 'API: Online';
        } else {
            handleApiError();
        }
    } catch (error) {
        console.error('Fetch Error:', error);
        handleApiError();
    }
}

function handleApiError() {
    document.getElementById('api-health').className = 'badge badge-offline';
    document.getElementById('api-health').textContent = 'API: Offline';
}

function startPolling() {
    fetchData(); // Initial fetch
    setInterval(fetchData, REFRESH_INTERVAL);
}

/**
 * UI Rendering
 */
function renderUI() {
    // Update Stats
    document.getElementById('count-events').textContent = state.events.length;
    document.getElementById('count-alerts').textContent = state.alerts.length;

    // Render Events Feed
    const eventsContainer = document.getElementById('events-feed');
    if (state.events.length === 0) {
        eventsContainer.innerHTML = '<div style="text-align:center; padding:2rem; color:var(--text-muted);">No logs detected.</div>';
    } else {
        eventsContainer.innerHTML = state.events.slice(0, 20).map(event => `
            <div class="event-item severity-${(event.severity || 'LOW').toLowerCase()}">
                <div class="event-header">
                    <span class="event-type">${event.eventType}</span>
                    <span class="event-device">${event.deviceId}</span>
                </div>
                <div class="event-msg">
                    ${formatEventMessage(event)}
                </div>
                <span class="event-time">${new Date(event.timestamp || Date.now()).toLocaleTimeString()}</span>
            </div>
        `).join('');
    }

    // Render Alerts Feed
    const alertsContainer = document.getElementById('alerts-feed');
    if (state.alerts.length === 0) {
        alertsContainer.innerHTML = '<div style="text-align:center; padding:2rem; color:var(--text-muted);">Scanning for anomalies...</div>';
    } else {
        alertsContainer.innerHTML = state.alerts.slice(0, 20).map(alert => `
            <div class="event-item severity-high">
                <div class="event-header">
                    <span class="event-type" style="color:var(--danger)">THREAT DETECTED</span>
                    <span class="event-device">${alert.deviceId}</span>
                </div>
                <div class="event-msg">${alert.description}</div>
                <div style="display:flex; justify-content:space-between; align-items:center; margin-top:0.5rem;">
                    <span class="event-time">${new Date(alert.timestamp).toLocaleTimeString()}</span>
                    <span class="badge" style="background:rgba(239,68,68,0.1); color:var(--danger); border:1px solid rgba(239,68,68,0.2)">
                        Source: ${alert.source || 'ANOMALY_ENGINE'}
                    </span>
                </div>
            </div>
        `).join('');
    }
}

function formatEventMessage(event) {
    if (event.eventType === 'FILE_CHANGED') {
        return `File <code>${event.filename || 'unknown'}</code> was modified.`;
    }
    if (event.metadata) {
        return event.metadata;
    }
    return `Security event reported from device.`;
}

function refreshData() {
    fetchData();
}
