import sys
import unittest
from unittest.mock import MagicMock, patch
import json

# Mock kafka dependency
mock_kafka = MagicMock()
sys.modules["kafka"] = mock_kafka
sys.modules["kafka.errors"] = MagicMock()

import send_mail

class TestMailingService(unittest.TestCase):

    @patch('smtplib.SMTP')
    def test_send_alert_email_high_severity(self, mock_smtp):
        # Setup mock
        mock_server = MagicMock()
        mock_smtp.return_value.__enter__.return_value = mock_server
        
        # Test alert
        alert = {
            "severity": "HIGH",
            "deviceId": "WS-001",
            "eventType": "LOGIN_FAILURE",
            "description": "Multiple failed logins",
            "timestamp": "2025-12-18 15:33:00"
        }
        
        # Execute
        send_mail.send_alert_email(alert)
        
        # Verify
        mock_smtp.assert_called_with(send_mail.SMTP_HOST, send_mail.SMTP_PORT)
        self.assertTrue(mock_server.send_message.called)

    @patch('smtplib.SMTP')
    def test_send_alert_email_low_severity(self, mock_smtp):
        # Test alert
        alert = {
            "severity": "INFO",
            "deviceId": "WS-001"
        }
        
        # Execute
        send_mail.send_alert_email(alert)
        
        # Verify
        self.assertFalse(mock_smtp.called)

if __name__ == '__main__':
    unittest.main()
