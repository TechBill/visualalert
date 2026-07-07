# VisualAlert

## Advanced Visual Notification System for Hubitat

VisualAlert is a comprehensive visual notification system designed specifically for deaf and hard of hearing users. It provides reliable, customizable visual alerts for Hubitat home automation systems.

[VisualAlert](https://github.com/TechBill/visualalert)

## Features

- **Multiple Alert Patterns**: Simple Flash, Doorbell, Emergency, Strobe, Standby, and custom patterns
- **Universal Compatibility**: Works with ALL switchable devices in Hubitat:
  - Smart bulbs (Zigbee, Z-Wave, LAN)
  - Wall switches and dimmers (Zigbee, Z-Wave)
  - Smart outlets and plugs
  - Any device with on/off capability
- **Color Support**: Use different colors for different types of alerts (for color-capable devices)
- **Dimming Support**: Control brightness levels for dimmable devices
- **Advanced Triggers**: Motion, contact, switches, doorbells, water sensors, smoke/CO detectors
- **Scheduling**: Set specific times and days when alerts should be active
- **Conditions**: Restrict alerts based on modes, presence, illuminance, and other conditions
- **Failsafe Mechanisms**: Ensure devices return to normal state after alerts
- **Preview Functionality**: Test patterns before saving
- **Reliability Features**: Timeout protection, error handling, and state restoration

## Installation

### Method 1: Hubitat Package Manager (Recommended)

If VisualAlert is already listed in HPM:

1. Go to the **Apps** page in your Hubitat hub
2. Click **+Add User App**
3. Launch **Hubitat Package Manager**
4. Click **Install**
5. Choose **Search by Keyword**
6. Enter **VisualAlert**
7. Follow the prompts to install

### Method 2: Hubitat Package Manager - Manual via URL

If VisualAlert is not listed in search yet:

1. Go to the **Apps** page in your Hubitat hub
2. Click **+Add User App**
3. Launch **Hubitat Package Manager**
4. Click **Install**
5. Choose **From a URL**
6. Paste the following URL:

   ```text
   https://raw.githubusercontent.com/TechBill/visualalert/master/packageManifest.json
   ```

7. Click **Next** and follow the install prompts

### Method 3: Manual Installation via Apps Code

1. In your Hubitat hub, go to **Apps Code**
2. Click **+New App**
3. Paste the code from `VisualAlert.groovy`, then click **Save**
4. Repeat for `VisualAlertChild.groovy`

## Usage

### Setting Up Your First Alert

1. Go to the **Apps** page in your Hubitat hub
2. Click **+Add User App**
3. Click **VisualAlert**
4. Configure any global settings and click **Done** to add VisualAlert to your Apps list
5. Go back to the **Apps** page and click on the installed **VisualAlert** app
6. Click **Create New Alert**
7. Enter a name for your alert
8. Select the devices you want to use for this alert (any switchable device)
9. Select at least one trigger source (motion sensor, contact sensor, etc.)
10. Configure the alert pattern
11. Set any schedule or condition restrictions
12. Configure advanced settings if needed
13. Click **Done**

### Alert Types

- **Simple Flash**: Basic on/off flashing pattern
- **Doorbell**: Two quick flashes followed by a pause
- **Emergency**: Rapid flashing for urgent situations (always used automatically for smoke/CO triggers)
- **Strobe**: Three rapid flashes followed by a short pause
- **Standby**: On for 3 seconds, off for 3 seconds
- **Custom**: Define your own ON/OFF sequence with millisecond timing

### Device Compatibility

VisualAlert works with any device that has on/off capability:

- **Smart Bulbs**: Full support including color and brightness control (if the bulb supports it)
- **Wall Switches**: On/off control for regular ceiling lights
- **Wall Dimmers**: On/off and brightness control for dimmable ceiling lights
- **Smart Outlets/Plugs**: Control any device plugged into them
- **Other Switchable Devices**: Any device with on/off capability

### Tips for Effective Alerts

- Use different colors for different types of alerts (with color-capable bulbs)
- Use different brightness levels for different priorities (with dimmable devices)
- Place devices in strategic locations where you'll notice them
- Consider using multiple devices for important alerts
- Use the scheduling feature to avoid alerts when not needed
- Use illuminance restrictions to make alerts brighter in bright rooms

## Advanced Configuration

### Pattern Customization

You can create custom patterns by:

1. Selecting "Custom" as the pattern type
2. Setting the number of flashes
3. Adjusting the flash duration and pause duration
4. Selecting a color (for color-capable devices)
5. Setting brightness level (for dimmable devices)

## Troubleshooting

### Devices Not Alerting

- Ensure the devices are online and working properly
- Check that your trigger devices are functioning correctly
- Verify that the alert is not restricted by schedule or conditions
- Check the Hubitat logs for any error messages

### Patterns Not Working as Expected

- Try previewing the pattern to see how it looks
- Adjust the timing settings for better visibility
- Ensure color and level settings are compatible with your devices
- Reset the alert by saving it again

## Support and Feedback

For support, feature requests, or to report bugs, please [open an issue](https://community.hubitat.com/t/introducing-visualalert-a-smart-flashing-system-designed-by-a-deaf-user-for-real-life-needs/152512) on Hubitat Community Forum.

## License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.
