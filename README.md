# VisualAlert

![Version](https://img.shields.io/badge/version-1.1.2-blue.svg)

## Advanced Visual Notification System for Hubitat

VisualAlert is a comprehensive visual notification system designed specifically for deaf and hard of hearing users. It provides reliable, customizable visual alerts for Hubitat home automation systems.

[VisualAlert Repository](https://github.com/TechBill/visualalert)

## Features

- **Multiple Alert Patterns**: Simple Flash, Doorbell, Emergency, Strobe, Standby, and Custom patterns.
- **Universal Compatibility**: Works with ALL switchable devices in Hubitat:
  * Smart bulbs (Zigbee, Z-Wave, LAN)
  * Wall switches and dimmers (Zigbee, Z-Wave)
  * Smart outlets and plugs
  * Any device with on/off capability
- **Color Support**: Use different colors for different types of alerts (for color-capable devices).
- **Dimming Support**: Control brightness levels for dimmable devices.
- **Advanced Triggers**: Switches, Buttons, Motion Sensors, Contact Sensors, Smoke Detectors, CO Detectors, Water Sensors.
- **Scheduling**: Set specific times and days when alerts should be active.
- **Conditions**: Restrict alerts based on modes, presence, and illuminance.
- **Failsafe Mechanisms**: Ensure devices return to their previous state after alerts (optional).
- **Test Pattern Functionality**: Test configured patterns directly from the child app page.
- **Child Alert Disabling**: Optionally disable individual child alerts using a dedicated switch.
- **Reliability Features**: Timeout protection and state restoration.
- **Custom UI Styling**: Features styled headers and titles for better readability within the Hubitat UI.

## Repository Structure

```
VisualAlert/
├── VisualAlert.groovy           # Parent App
├── VisualAlertChild.groovy      # Child App
└── manifest.json                # Hubitat Package Manager Manifest
```

## Installation

### Method 1: Hubitat Package Manager (Recommended)

1. In your Hubitat hub, navigate to **Apps**.
2. Click **Hubitat Package Manager**.
3. Click **Install**.
4. Select **Search by Keywords**.
5. Enter "VisualAlert" and click **Next**.
6. Select **VisualAlert** from the search results.
7. Click **Install** and follow the on-screen prompts.

**If VisualAlert doesn't show up in search:**

1. Open Hubitat Package Manager.
2. Go to **Add a Custom Repository**.
3. Paste this URL:

```
https://raw.githubusercontent.com/TechBill/visualalert/main/manifest.json
```

4. After adding it, repeat the install steps above.

### Method 2: Manual Installation

1. In your Hubitat hub, navigate to **Apps Code**.
2. Click **+New App**.
3. Paste the code from `VisualAlert.groovy`. Click **Save**.
4. Click **+New App** again.
5. Paste the code from `VisualAlertChild.groovy`. Click **Save**.
6. Navigate to **Apps** in the main menu.
7. Click **+Add User App**.
8. Select **VisualAlert** from the list and click **Done**.

## Usage

### Setting Up Your First Alert

1. Go to the **Apps** page in your Hubitat hub.
2. Click on the installed **VisualAlert** app.
3. Configure any global settings (Logging, Notifications) if desired.
4. Click **Create VisualAlert Child**.
5. Enter a unique name for this specific alert (e.g., "Front Door Motion Alert").
6. Select the **Devices** you want to flash for this alert.
7. Select at least one **Trigger Source** (Switch, Button, Sensor). Configure trigger details if necessary (e.g., button number, sensor state).
8. Configure the **Alert Pattern**:
    * Go to **Pattern Configuration**.
    * Select a **Pattern Type**.
    * Set the **Number of Repeats** (0 for indefinite).
    * If using "Custom", set the ON/OFF durations in **milliseconds**.
    * Configure **Device Properties** like color, brightness, and restore state behavior.
    * Click **Done** to return to the main child page.
9. Optionally, configure **Stop Alert Triggers** or the **Disable Switch**.
10. Optionally, configure **Schedule & Conditions**.
11. Optionally, configure **Advanced Settings**.
12. Click **Done** to save the child alert configuration.

## Alert Pattern Types

- **Simple Flash**: Basic on/off flashing pattern (default 1000ms on, 1000ms off).
- **Doorbell**: Two quick flashes followed by a pause (300ms on/off, 300ms on/1000ms off).
- **Emergency**: Rapid flashing sequence (300ms on, 300ms off).
- **Strobe**: Three rapid flashes followed by a longer pause (200ms on/off x3, then 1500ms off).
- **Standby**: Longer on/off cycle (3000ms on, 3000ms off).
- **Custom**: Define your own ON and OFF durations in milliseconds.

## Device Compatibility

VisualAlert works with any device that has on/off capability:

- **Smart Bulbs**: Full support including color and brightness control (if the bulb supports it).
- **Wall Switches**: On/off control for regular ceiling lights.
- **Wall Dimmers**: On/off and brightness control for dimmable ceiling lights.
- **Smart Outlets/Plugs**: Control any device plugged into them.
- **Other Switchable Devices**: Any device with on/off capability.

## Tips for Effective Alerts

- Use different colors for different types of alerts (with color-capable bulbs).
- Use different brightness levels for different priorities (with dimmable devices).
- Place devices in strategic locations where you'll notice them.
- Consider using multiple devices for important alerts.
- Use the scheduling feature to avoid alerts when not needed.
- Use illuminance restrictions to only trigger alerts in certain light conditions.

## Advanced Configuration

### Pattern Customization

You can create custom patterns by:

1. Selecting "Custom" as the pattern type.
2. Setting the **Flash ON Duration (milliseconds)**.
3. Setting the **Flash OFF Duration (milliseconds)**.
4. Selecting a color (for color-capable devices).
5. Setting brightness level (for dimmable devices).
6. Setting the number of repeats (0 for indefinite).

## Troubleshooting

### Devices Not Alerting

- Ensure the target devices are online and working properly in Hubitat.
- Check that your trigger devices are functioning correctly and sending events.
- Verify that the alert is not restricted by schedule or conditions (Mode, Time, Days, Presence, Illuminance).
- Check if the child alert is disabled by the optional Disable Switch.
- Check the Hubitat logs (**Logs** page) for any error messages related to VisualAlert or the devices involved. Enable debug logging in the app settings for more detail.

### Patterns Not Working as Expected

- Use the **Test Alert** buttons on the child app page to test the pattern directly.
- Adjust the timing settings (especially for Custom patterns) for better visibility or device compatibility.
- Ensure color and level settings are compatible with your target devices.
- Ensure the "Restore Previous State" option is set as desired.
- Reset the alert by clicking **Done** to save it again.

## Support and Feedback

For support, feature requests, or to report bugs, please [open an issue](https://github.com/TechBill/visualalert/issues)

## Donations

Donations are always appreciated!
- [💸 Donate via PayPal](https://paypal.me/techbill?country.x=US&locale.x=en_US)
- [☕ Buy Me a Coffee](https://www.buymeacoffee.com/techbill)

## License

This project is licensed under the Apache License 2.0.
