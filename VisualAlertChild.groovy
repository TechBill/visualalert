/**
 *  VisualAlert Child
 *
 *  Advanced alert configuration for the VisualAlert system
 *  Manages individual alert patterns and triggers
 *
 *  Copyright 2024
 *  Licensed under the Apache License, Version 2.0
 *
 *  Version: 1.3.0
 */

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

definition(
    name: "VisualAlert Child",
    namespace: "TechBill",
    author: "Bill Fleming",
    description: "Configure individual visual alert patterns and triggers",
    parent: "TechBill:VisualAlert",
    category: "Convenience",
    iconUrl: "",
    iconX2Url: "",
    iconX3Url: ""
)

preferences {
    page(name: "mainPage")
    page(name: "patternPage")
    page(name: "schedulePage")
    page(name: "advancedPage")
}

def mainPage() {
    dynamicPage(name: "mainPage", title: "", install: true, uninstall: true, nextPage: null) {
        display()
        section(getFormat("header-blue", "${getImage("Blank")}" + "Alert Name & Devices")) {
            input "alertName", "text",
                title: "VisualAlert Child Name",
                required: true,
                submitOnChange: true

            input "devices", "capability.switch",
                title: "Select Devices",
                multiple: true,
                required: true,
                description: "Works with any switchable device: smart bulbs, Zigbee/Z-Wave switches, dimmers, outlets, etc."
        }

        section(getFormat("header-blue", "${getImage("Blank")}" + "Trigger Sources")) {
            paragraph "Select switches, buttons, or sensors (Contact, Motion, Smoke, CO, Water) that will activate this alert"

            input "triggerSwitches", "capability.switch",
                title: "Switches",
                multiple: true,
                required: false,
                submitOnChange: true,
                description: "Select switches that can trigger this alert"
            // Dynamic inputs for ON/OFF selection for each trigger switch
            if (triggerSwitches) {
                triggerSwitches.each { device ->
                    input "switchTrigger_${device.id}", "enum",
                        title: "${device.displayName} Trigger",
                        options: ["ON", "OFF"],
                        required: true,
                        defaultValue: "ON",
                        description: "Trigger when this switch turns ON or OFF?"
                }
            }


            input "buttons", "capability.pushableButton",
                title: "Buttons",
                multiple: true,
                required: false,
                submitOnChange: true,
                description: "Select button devices that can trigger this alert"

            if (buttons) {
                // Generate button options
                def buttonOptions = [:]
                buttons.each { device ->
                    def numberOfButtons = device.currentValue("numberOfButtons") ?: 1
                    for (def i = 1; i <= numberOfButtons; i++) {
                        buttonOptions["${device.id}:$i"] = "${device.displayName}: Button ${i}"
                    }
                }

                if (buttonOptions.size() > 0) {
                    paragraph "<i>Available Buttons:</i>"
                    buttons.each { device ->
                        def numberOfButtons = device.currentValue("numberOfButtons") ?: 1
                        paragraph "• ${device.displayName} has ${numberOfButtons} button${numberOfButtons > 1 ? 's' : ''}"
                    }

                    input "selectedButtons", "enum",
                        title: "Select Button(s) to Trigger Alert",
                        options: buttonOptions,
                        required: false,
                        multiple: true,
                        submitOnChange: true,
                        description: "Pick which button(s) will trigger the alert when pressed"
                }

               
                if (selectedButtons) {
                    paragraph "<b>Selected Trigger Button(s):</b>"
                    selectedButtons.each { buttonId ->
                        def (deviceId, buttonNumber) = buttonId.split(":")
                        def device = buttons.find { it.id == deviceId }
                        if (device) {
                            paragraph "• ${device.displayName}: Button ${buttonNumber} will trigger the alert"
                        }
                    }
                }
            }
// Contact Sensors
input "contactSensors", "capability.contactSensor",
    title: "Contact Sensors",
    multiple: true,
    required: false,
    submitOnChange: true,
    description: "Select contact sensors that can trigger this alert when opened"

// Motion Sensors
input "motionSensors", "capability.motionSensor",
    title: "Motion Sensors",
    multiple: true,
    required: false,
    submitOnChange: true,
    description: "Select motion sensors that can trigger this alert when motion becomes active"
            // Smoke Sensors
            input "smokeSensors", "capability.smokeDetector",
                title: "Smoke Sensors",
                multiple: true,
                required: false,
                submitOnChange: true,
                description: "Select smoke sensors that can trigger this alert when smoke is detected"

            // CO Sensors
            input "coSensors", "capability.carbonMonoxideDetector",
                title: "Carbon Monoxide Sensors",
                multiple: true,
                required: false,
                submitOnChange: true,
                description: "Select CO sensors that can trigger this alert when CO is detected"
            // Water Sensors
            input "waterSensors", "capability.waterSensor",
                title: "Water Sensors",
                multiple: true,
                required: false,
                submitOnChange: true,
                description: "Select water sensors that can trigger this alert when water is detected"
        }

        section(getFormat("header-blue", "${getImage("Blank")}" + "Stop Alert | Enable/Disable VisualAlert Trigger")) {
            paragraph "Optional: Select buttons that will stop this alert when pressed"

            input "stopButtonDevices", "capability.pushableButton",
                title: "Select Button(s)",
                multiple: true,
                required: false,
                submitOnChange: true,
                description: "Choose button devices that can stop this alert"

            paragraph "Optional: Select switch that will enable/disable this child app."
            input "disableSwitch", "capability.switch",
                title: "Disable Alert with Switch?",
                required: false,
                multiple: false, // Only allow one switch for this
                submitOnChange: true, // Ensure this is true to show the next input dynamically
                description: "Optional: Select a switch to enable/disable this alert."

            // Dynamic input for ON/OFF condition for the disable switch
            if (disableSwitch) {
                input "disableCondition", "enum",
                    title: "Disable When Switch Is",
                    options: ["ON", "OFF"],
                    required: true,
                    defaultValue: "ON",
                    description: "Disable the alert when the selected switch turns ON or OFF?"
            }

            if (stopButtonDevices) {
                // Generate button options
                def buttonOptions = [:]
                stopButtonDevices.each { device ->
                    def numberOfButtons = device.currentValue("numberOfButtons") ?: 1
                    for (def i = 1; i <= numberOfButtons; i++) {
                        buttonOptions["${device.id}:$i"] = "${device.displayName}: Button ${i}"
                    }
                }

                if (buttonOptions.size() > 0) {
                    paragraph "<i>Available Buttons:</i>"
                    stopButtonDevices.each { device ->
                        def numberOfButtons = device.currentValue("numberOfButtons") ?: 1
                        paragraph "• ${device.displayName} has ${numberOfButtons} button${numberOfButtons > 1 ? 's' : ''}"
                    }

                    input "selectedStopButtons", "enum",
                        title: "Select Button(s) to Stop Alert",
                        options: buttonOptions,
                        required: false,
                        multiple: true,
                        submitOnChange: true,
                        description: "Pick which button(s) will stop the alert when pressed"
                }
            }

            if (selectedStopButtons) {
                paragraph "<b>Selected Stop Button(s):</b>"
                selectedStopButtons.each { buttonId ->
                    def (deviceId, buttonNumber) = buttonId.split(":")
                    def device = stopButtonDevices.find { it.id == deviceId }
                    if (device) {
                        paragraph "• ${device.displayName}: Button ${buttonNumber} will stop the alert"
                    }
                }
            }
        }

        section(getFormat("header-blue", "${getImage("Blank")}" + "Pattern Configuration")) {
            href "patternPage", title: "Configure Alert Pattern", description: "Set up how devices will alert"
        }

        section(getFormat("header-blue", "${getImage("Blank")}" + "Test Alert")) {
             paragraph "Use these buttons to test the currently configured alert pattern on the selected devices."
             input "btnTestAlert", "button", title: "Test Alert Pattern"
             input "btnStopTest", "button", title: "Stop Test"
        }

        section(getFormat("header-blue", "${getImage("Blank")}" + "Schedule & Conditions")) {
            href "schedulePage", title: "Set Schedule & Conditions", description: "When alerts should be active"
        }

        section(getFormat("header-blue", "${getImage("Blank")}" + "Advanced Settings")) {
            href "advancedPage", title: "Advanced Configuration", description: "Failsafe and notification settings"
        }

        section("Support") {
            paragraph "Donations are always appreciated!"
            paragraph "<a href='https://paypal.me/techbill?country.x=US&locale.x=en_US' target='_blank'>💸 Donate via PayPal</a>"
            paragraph "<a href='https://www.buymeacoffee.com/techbill' target='_blank'>☕ Buy Me a Coffee</a>"
        }
    }
}

def patternPage() {
    dynamicPage(name: "patternPage", title: "", nextPage: "mainPage") {
        display()
        section(getFormat("header-blue", "${getImage("Blank")}" + "Pattern Type")) {
            input "patternType", "enum",
                title: "Pattern Type",
                options: ["Simple Flash", "Doorbell", "Emergency", "Strobe", "Standby", "Custom"],
                defaultValue: "Simple Flash",
                required: true,
                submitOnChange: true // Keep this to update the description dynamically

            // Display description based on selected pattern type
            if (settings.patternType == null || settings.patternType == "Simple Flash") { // Handle initial load default
                paragraph "<b>Simple Flash:</b> A single flash on, then off. Repeats based on 'Number of Repeats'."
            } else if (settings.patternType == "Doorbell") {
                paragraph "<b>Doorbell:</b> Two quick flashes followed by a pause. Repeats based on 'Number of Repeats'."
            } else if (settings.patternType == "Emergency") {
                paragraph "<b>Emergency:</b> Rapid flashing sequence. Repeats based on 'Number of Repeats'."
            } else if (settings.patternType == "Strobe") {
                paragraph "<b>Strobe:</b> Three rapid flashes followed by a short pause. Repeats based on 'Number of Repeats'."
            } else if (settings.patternType == "Standby") {
                paragraph "<b>Standby:</b> Device stays on for 3 seconds, then off for 3 seconds. Repeats based on 'Number of Repeats'."
            } else if (settings.patternType == "Custom") {
                paragraph "<b>Custom:</b> Define a sequence of ON and OFF steps with durations in milliseconds (e.g., ON:1000, OFF:500). Repeats based on 'Number of Repeats'."
            } else {
                 paragraph "Select a pattern type." // Default message
            }
        }

        section(getFormat("header-blue", "${getImage("Blank")}" + "Pattern Repetition & Timing")) {
            input "repeatCount", "number",
                title: "Number of Repeats (0 = indefinite)",
                required: true,
                defaultValue: 5, // Default to 5 repeats
                range: "0..100", // Limit to 100 repeats (adjust if needed)
                description: "Applies to all pattern types. 0 means the pattern repeats until stopped."

            if (patternType == "Custom") { // Only show sequence input for Custom pattern
                 paragraph "<b>Custom Sequence Definition</b>"
                 input "customPatternSequence", "textArea",
                    title: "Custom ON/OFF Sequence",
                    required: true,
                    defaultValue: "ON:1000, OFF:1000", // Default to a simple flash
                    description: "Define the custom alert sequence here."

                 // Add detailed instructions paragraph below the input
                 paragraph """<small><b>Instructions:</b><br>
                 • Enter a comma-separated list of ON and OFF steps.<br>
                 • Each step must be in the format <code>STATE:DURATION</code> (e.g., <code>ON:1000</code> or <code>OFF:500</code>).<br>
                 • <code>STATE</code> must be either <code>ON</code> or <code>OFF</code> (case-insensitive).<br>
                 • <code>DURATION</code> is the time in <b>milliseconds</b> (e.g., 1000ms = 1 second).<br>
                 • Do not include spaces within or immediately around the commas.<br>
                 <b>Example 1 (Simple Flash):</b> <code>ON:1000,OFF:1000</code><br>
                 <b>Example 2 (Quick Double Flash):</b> <code>ON:200,OFF:200,ON:200,OFF:1000</code><br>
                 </small>"""
            }
        }

        section(getFormat("header-blue", "${getImage("Blank")}" + "Device Properties")) {
            paragraph "Note: Color and level options only apply to devices that support these capabilities. Regular switches will simply turn on and off."

            input "useColor", "bool",
                title: "Use Color (for color-capable devices only)",
                defaultValue: false

            input "alertColor", "color",
                title: "Alert Color (for color-capable devices only)",
                required: false


            input "alertLevel", "number",
                title: "Alert Brightness Level (1-100, for dimmable devices only)",
                range: "1..100",
                required: false,
                defaultValue: 100

            input "offLevel", "number",
                title: "Off-State Brightness Level (0-100, used instead of full off)",
                range: "0..100",
                required: false,
                defaultValue: 0,
                description: "Set to 0 for complete off, or higher to dim instead of turning completely off"

            input "restorePrevious", "bool",
                title: "Restore Last State (disabled = all devices off after alert)",
                defaultValue: true
        }
    }
}

def schedulePage() {
    dynamicPage(name: "schedulePage", title: "", nextPage: "mainPage") {
        display()
        section(getFormat("header-blue", "${getImage("Blank")}" + "Active Hours")) {
            input "activeStart", "time",
                title: "Start Time",
                required: false

            input "activeEnd", "time",
                title: "End Time",
                required: false

            input "days", "enum",
                title: "Active Days",
                multiple: true,
                options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"],
                required: false
        }

        section(getFormat("header-blue", "${getImage("Blank")}" + "Conditions")) {
            input "modeRestriction", "mode",
                title: "Restrict to Modes",
                multiple: true,
                required: false

            input "presenceRestriction", "capability.presenceSensor",
                title: "Restrict to Presence",
                multiple: true,
                required: false

            input "illuminanceRestriction", "capability.illuminanceMeasurement",
                title: "Restrict Based on Illuminance",
                multiple: false,
                required: false

            input "illuminanceThreshold", "number",
                title: "Illuminance Threshold (lux)",
                required: false,
                defaultValue: 50,
                description: "Only activate when illuminance is below this value"
        }
    }
}

def advancedPage() {
    dynamicPage(name: "advancedPage", title: "", nextPage: "mainPage") {
        display()
        section(getFormat("header-blue", "${getImage("Blank")}" + "Failsafe Settings")) {
            input "timeout", "number",
                title: "Pattern Timeout (minutes)",
                required: true,
                defaultValue: 5

            input "retryCount", "number",
                title: "Retry Attempts",
                required: true,
                defaultValue: 3,
                range: "0..10",
                description: "How many times to retry turning a device back ON if it fails to respond during state restoration"

            input "cancelOnReverse", "bool",
                title: "Cancel Alert When Trigger Reverses",
                defaultValue: true,
                description: "Stop the alert when a trigger returns to normal (switch reverses, contact closes, motion goes inactive, sensor clears)"
        }

        section(getFormat("header-blue", "${getImage("Blank")}" + "Notifications")) {
            input "notifyStart", "bool",
                title: "Notify on Alert Start",
                defaultValue: true

            input "notifyEnd", "bool",
                title: "Notify on Alert End",
                defaultValue: true

            input "notificationDevices", "capability.notification",
                title: "Notification Devices",
                multiple: true,
                required: false,
                description: "Optional: Send notifications to these devices"
        }

        section(getFormat("header-blue", "${getImage("Blank")}" + "Logging")) {
            input "enableLogging", "bool",
                title: "Enable Debug Logging",
                defaultValue: false
        }
    }
}

// --- Lifecycle Methods ---
def installed() {
    logDebug "VisualAlert Child installed"
    app.updateLabel(alertName ?: "New Alert")
    initialize()
}

def updated() {
    logDebug "VisualAlert Child updated"
    app.updateLabel(alertName ?: "New Alert")
    unsubscribe()
    unschedule() // Ensure no old schedules persist (clears finalCleanupAndRestore, processAlertDevices etc.)
    unschedule("executeDevicePattern") // Explicitly clear old handler name
    initialize()
}

def initialize() {
    log.info "Initializing VisualAlert Child: ${alertName ?: 'Unnamed Alert'}"

    // Cross-execution flags live in atomicState so scheduled pattern steps and
    // event handlers running concurrently always see the current value.
    atomicState.isAlertRunning = false
    state.lastTriggerDevice = null

    // Remove stale keys left behind by older versions
    state.remove("isAlertRunning")
    state.remove("isDisabled")
    state.remove("previousStates")
    state.remove("currentAlertStates")

    // Store restorePrevious setting in state for reliable access (read via settings map)
    // Default to true only if the setting is null (not set), otherwise use the actual boolean value
    state.restorePreviousEnabled = (settings.restorePrevious != null) ? (settings.restorePrevious as Boolean) : true
    logDebug "initialize: Stored restorePreviousEnabled in state: ${state.restorePreviousEnabled}"

    // Initialize disabled state based on disableSwitch and disableCondition
    if (disableSwitch) {
        def conditionToDisable = settings.disableCondition ?: "ON" // Default to ON
        def currentSwitchValue = disableSwitch.currentValue("switch")
        atomicState.isDisabled = currentSwitchValue?.equalsIgnoreCase(conditionToDisable) ?: false // Be safe if current value is null
        logDebug "initialize: Disable switch selected. Condition='${conditionToDisable}', CurrentValue='${currentSwitchValue}'. Initial isDisabled state: ${atomicState.isDisabled}"
    } else {
        atomicState.isDisabled = false // No disable switch selected
        logDebug "initialize: No disable switch selected. Initial isDisabled state: false"
    }

    // Subscribe to events
    subscribeToEvents()
    unschedule("executeDevicePattern") // Explicitly clear old handler name during init too
}

// Reports whether this alert is currently running (called by the parent app's health check)
def isActive() {
    return atomicState.isAlertRunning ?: false
}

// cancelOnReverse defaults to true when the advanced page has never been saved
private boolean cancelOnReverseEnabled() {
    return (settings.cancelOnReverse != null) ? (settings.cancelOnReverse as Boolean) : true
}

def subscribeToEvents() {
    try {
        logDebug "*************** Subscribing to Events ***************"

        // Buttons - use single handler for both trigger and stop buttons
        def allButtonDevices = []
        if (buttons) allButtonDevices.addAll(buttons)
        if (stopButtonDevices) allButtonDevices.addAll(stopButtonDevices)
        allButtonDevices = allButtonDevices.unique { it.id } // Remove duplicates

        if (allButtonDevices) {
            logDebug "Setting up button subscriptions for ${allButtonDevices.size()} devices"
            allButtonDevices.each { device ->
                logDebug "Subscribing to button device: ${device.displayName} (ID: ${device.id})"
                subscribe(device, "pushed", buttonHandler)

                def numberOfButtons = device.currentValue("numberOfButtons") ?: 1
                logDebug "Device ${device.displayName} has ${numberOfButtons} button(s)"
            }
        }

        // Switches - Trigger (Subscribe to both ON and OFF)
        if (triggerSwitches) {
            logDebug "Setting up switch subscriptions for ${triggerSwitches.size()} devices"
            triggerSwitches.each { device ->
                logDebug "Subscribing to switch ON/OFF events for: ${device.displayName} (ID: ${device.id})"
                subscribe(device, "switch.on", switchHandler)
                subscribe(device, "switch.off", switchHandler)
            }
        }
        // If cancelOnReverse is enabled, also watch for sensors returning to normal
        boolean watchReverse = cancelOnReverseEnabled()

        // Contact Sensors - Trigger
        if (contactSensors) {
            logDebug "Setting up contact sensor subscriptions for ${contactSensors.size()} devices"
            contactSensors.each { device ->
                logDebug "Subscribing to contact sensor: ${device.displayName} (ID: ${device.id})"
                subscribe(device, "contact.open", sensorHandler) // Subscribe to 'open' event
                if (watchReverse) subscribe(device, "contact.closed", sensorHandler)
            }
        }

        // Motion Sensors - Trigger
        if (motionSensors) {
            logDebug "Setting up motion sensor subscriptions for ${motionSensors.size()} devices"
            motionSensors.each { device ->
                logDebug "Subscribing to motion sensor: ${device.displayName} (ID: ${device.id})"
                subscribe(device, "motion.active", sensorHandler) // Subscribe to 'active' event
                if (watchReverse) subscribe(device, "motion.inactive", sensorHandler)
            }
        }

        // Smoke Sensors - Trigger
        if (smokeSensors) {
            logDebug "Setting up smoke sensor subscriptions for ${smokeSensors.size()} devices"
            smokeSensors.each { device ->
                logDebug "Subscribing to smoke sensor: ${device.displayName} (ID: ${device.id})"
                subscribe(device, "smoke.detected", sensorHandler) // Subscribe to 'detected' event
                if (watchReverse) subscribe(device, "smoke.clear", sensorHandler)
            }
        }

        // CO Sensors - Trigger
        if (coSensors) {
            logDebug "Setting up CO sensor subscriptions for ${coSensors.size()} devices"
            coSensors.each { device ->
                logDebug "Subscribing to CO sensor: ${device.displayName} (ID: ${device.id})"
                subscribe(device, "carbonMonoxide.detected", sensorHandler) // Subscribe to 'detected' event
                if (watchReverse) subscribe(device, "carbonMonoxide.clear", sensorHandler)
            }
        }

        // Water Sensors - Trigger
        if (waterSensors) {
            logDebug "Setting up water sensor subscriptions for ${waterSensors.size()} devices"
            waterSensors.each { device ->
                logDebug "Subscribing to water sensor: ${device.displayName} (ID: ${device.id})"
                subscribe(device, "water.wet", sensorHandler) // Subscribe to 'wet' event
                if (watchReverse) subscribe(device, "water.dry", sensorHandler)
            }
        }

        // Subscribe to the disable switch
        if (disableSwitch) {
            logDebug "Subscribing to disable switch: ${disableSwitch.displayName}"
            subscribe(disableSwitch, "switch", disableSwitchHandler)
        }

        logDebug "Subscribed to all event sources"
        logDebug "Current configuration:"
        logDebug "Selected Trigger Buttons: ${selectedButtons}"
        logDebug "Selected Stop Buttons: ${selectedStopButtons}"
    } catch (e) {
        log.error "Error subscribing to events: ${e}"
    }
} // End of subscribeToEvents

// Handler for the disable switch
def disableSwitchHandler(evt) {
    logDebug "disableSwitchHandler called with event: ${evt.value} from ${evt.displayName}"

    // Get the configured condition ("ON" or "OFF", default to "ON" if somehow not set)
    def conditionToDisable = settings.disableCondition ?: "ON"
    def eventValue = evt.value // "on" or "off"

    logDebug "Checking event '${eventValue}' against disable condition '${conditionToDisable}'"

    // Check if the current event value matches the condition that should disable the alert
    if (eventValue.equalsIgnoreCase(conditionToDisable)) {
        if (!atomicState.isDisabled) { // Only log and stop if changing state to disabled
            atomicState.isDisabled = true
            logDebug "Alerts DISABLED because ${evt.displayName} turned ${eventValue} (matches condition '${conditionToDisable}')"
            // Stop any active alert immediately when disabled
            stopAlertImmediate(reason: "disabled by switch ${evt.displayName}")
        } else {
            logDebug "Alerts remain disabled (switch ${evt.displayName} is ${eventValue})"
        }
    } else {
        if (atomicState.isDisabled) { // Only log if changing state to enabled
            atomicState.isDisabled = false
            logDebug "Alerts ENABLED because ${evt.displayName} turned ${eventValue} (does not match condition '${conditionToDisable}')"
        } else {
            logDebug "Alerts remain enabled (switch ${evt.displayName} is ${eventValue})"
        }
    }
}

// --- Event Handlers ---

// Handles physical button presses for trigger and stop
def buttonHandler(evt) {
    logDebug "*************** Button Handler ***************"
    logDebug "Button Event: device=${evt.device.displayName}, deviceId=${evt.deviceId}, value=${evt.value}"

    def deviceAndButton = "${evt.deviceId}:${evt.value}"
    logDebug "Button press detected - deviceAndButton: '${deviceAndButton}'"

    // Convert to string and trim for comparison
    def buttonToCheck = deviceAndButton.toString().trim()
    def selectedButtonsList = selectedButtons?.collect { it.toString().trim() }
    def selectedStopButtonsList = selectedStopButtons?.collect { it.toString().trim() }

    // First check if this is a stop button - stop must work even while disabled
    if (selectedStopButtonsList?.contains(buttonToCheck)) {
        log.info "Stop button match found! Button: ${buttonToCheck}."
        stopAlertImmediate(reason: "Stop button pressed: ${evt.device.displayName} Button ${evt.value}", fromButton: true)
        return
    }

    // Check if disabled (only blocks trigger buttons, not stop buttons)
    if (atomicState.isDisabled) {
        logDebug "Button event ignored: Alert is disabled by switch."
        return
    }

    // Then check if this is a trigger button
    if (selectedButtonsList?.contains(buttonToCheck)) {
        log.info "Trigger button match found! Button: ${buttonToCheck}"
        state.lastTriggerDevice = evt.deviceId
        // Call startAlert directly - it will handle stopping existing alert and checking conditions
        startAlert("Button ${evt.device.displayName} pressed")
        return
    }

    logDebug "Button press ${buttonToCheck} did not match any configured trigger or stop buttons."
}

// Handles button presses from the app UI (Preview/Stop Preview)
def appButtonHandler(btn) {
    logDebug "*************** App Button pressed: $btn ***************"

    switch(btn) {
        case "btnTestAlert":
            logDebug "Test Alert button pressed - starting alert"
            // Call startAlert directly, it handles conditions and stopping existing alerts
            startAlert("Test Alert button pressed")
            break

        case "btnStopTest":
            logDebug "Stop Test button pressed - setting flags and calling stopAlertImmediate"
            // Call stopAlertImmediate mainly for cleanup, indicate source
            stopAlertImmediate(reason: "Stop Test button pressed", fromButton: true)
            break

        default:
            logDebug "Unknown app button: $btn"
    }
}

// Handles switch 'on' and 'off' events for trigger switches
def switchHandler(evt) {
    // Check if disabled
    if (atomicState.isDisabled) {
        logDebug "Switch event ignored: Alert is disabled by switch."
        return
    }
    logDebug "*************** Switch Handler ***************"
    logDebug "Switch Event: device=${evt.device.displayName}, deviceId=${evt.deviceId}, value=${evt.value}"

    def deviceId = evt.deviceId
    def eventValue = evt.value // "on" or "off"
    def deviceName = evt.device.displayName

    // Get the configured trigger condition ("ON" or "OFF") for this specific switch
    def settingName = "switchTrigger_${deviceId}"
    def configuredTrigger = settings[settingName] // Access setting dynamically

    // Check if a trigger condition is actually configured for this switch
    if (!configuredTrigger) {
        logDebug "Switch event ignored: No trigger condition configured for ${deviceName} (Setting: ${settingName})"
        return // Exit if no configuration found for this specific switch
    }

    logDebug "Device ${deviceName}: Event='${eventValue}', Configured Trigger='${configuredTrigger}'"

    // Check if the event value matches the configured trigger condition (case-insensitive)
    if (eventValue.equalsIgnoreCase(configuredTrigger)) {
        log.info "Trigger condition met for ${deviceName}: Event '${eventValue}' matches configured '${configuredTrigger}'"
        state.lastTriggerDevice = deviceId
        startAlert("Switch ${deviceName} turned ${eventValue}") // Use eventValue in reason
    } else if (cancelOnReverseEnabled() && atomicState.isAlertRunning &&
               state.lastTriggerDevice?.toString() == deviceId?.toString()) {
        // The switch that triggered the running alert reversed (e.g., configured for ON and it turned OFF)
        log.info "Trigger reversed: ${deviceName} turned ${eventValue}. Cancelling alert (cancelOnReverse enabled)."
        stopAlertImmediate(reason: "trigger reversed: ${deviceName} turned ${eventValue}")
    } else {
        // Log if the event occurred but didn't match the configured trigger (e.g., switch turned ON but configured for OFF)
        logDebug "Switch event ignored: Event '${eventValue}' does not match configured trigger '${configuredTrigger}' for ${deviceName}"
    }
}
// Handles sensor trigger events and (when cancelOnReverse is enabled) return-to-normal events
def sensorHandler(evt) {
    // Check if disabled
    if (atomicState.isDisabled) {
        logDebug "Sensor event ignored: Alert is disabled by switch."
        return
    }
    logDebug "*************** Sensor Handler ***************"
    logDebug "Sensor Event: device=${evt.device.displayName}, deviceId=${evt.deviceId}, name=${evt.name}, value=${evt.value}"

    def triggerReason = null
    boolean emergency = false // Smoke/CO are life-safety: force the Emergency pattern
    // Check for smoke detection
    if (evt.name == "smoke" && evt.value == "detected") {
        triggerReason = "Smoke detected by ${evt.device.displayName}"
        emergency = true
    }
    // Check for CO detection
    else if (evt.name == "carbonMonoxide" && evt.value == "detected") {
        triggerReason = "CO detected by ${evt.device.displayName}"
        emergency = true
    }
    // Check for water detection
    else if (evt.name == "water" && evt.value == "wet") {
        triggerReason = "Water detected by ${evt.device.displayName}"
    }
    // Check for contact opening
    else if (evt.name == "contact" && evt.value == "open") {
        triggerReason = "Contact opened on ${evt.device.displayName}"
    }
    // Check for motion activation
    else if (evt.name == "motion" && evt.value == "active") {
        triggerReason = "Motion detected by ${evt.device.displayName}"
    }

    // If a valid trigger was found, start the alert
    if (triggerReason) {
        log.info triggerReason
        state.lastTriggerDevice = evt.deviceId
        // Call startAlert - it handles conditions and stopping existing alerts
        startAlert(triggerReason, emergency)
        return
    }

    // Return-to-normal events cancel a running alert when cancelOnReverse is enabled,
    // but only when the reversing device is the one that triggered the alert
    def reverseValues = [smoke: "clear", carbonMonoxide: "clear", water: "dry", contact: "closed", motion: "inactive"]
    if (reverseValues[evt.name] == evt.value) {
        if (cancelOnReverseEnabled() && atomicState.isAlertRunning &&
            state.lastTriggerDevice?.toString() == evt.deviceId?.toString()) {
            log.info "Trigger reversed: ${evt.device.displayName} ${evt.name} is ${evt.value}. Cancelling alert (cancelOnReverse enabled)."
            stopAlertImmediate(reason: "trigger reversed: ${evt.device.displayName} ${evt.name} ${evt.value}")
        } else {
            logDebug "Return-to-normal event ignored (not the triggering device, no alert running, or cancelOnReverse disabled)"
        }
        return
    }

    // Log if the event wasn't a state we care about
    logDebug "Sensor event ignored (name: ${evt.name}, value: ${evt.value})"
}

// --- Core Alert Logic ---

// Checks if conditions (time, day, mode, presence, illuminance) are met
def isValidTrigger() {
    // Check time restrictions
    if (activeStart && activeEnd) {
        try {
            def startTime = toDateTime(activeStart)
            def endTime = toDateTime(activeEnd)
            boolean withinWindow
            if (startTime.time <= endTime.time) {
                withinWindow = timeOfDayIsBetween(startTime, endTime, new Date(), location.timeZone)
            } else {
                // Window crosses midnight (e.g., 22:00 - 06:00): active when NOT in the inverted window
                withinWindow = !timeOfDayIsBetween(endTime, startTime, new Date(), location.timeZone)
            }
            if (!withinWindow) {
                logDebug "Outside active hours"
                return false
            }
        } catch (e) {
            log.warn "Error parsing time restriction: ${e.message}"
        }
    }

    // Check day restrictions
    if (days && days.size() > 0) {
        def today = new Date().format("EEEE", location.timeZone)
        if (!days.contains(today)) {
            logDebug "Not an active day: ${today}"
            return false
        }
    }

    // Check mode restrictions
    if (modeRestriction && modeRestriction.size() > 0) {
        if (!modeRestriction.contains(location.mode)) {
            logDebug "Mode not active: ${location.mode}"
            return false
        }
    }

    // Check presence restrictions
    if (presenceRestriction && presenceRestriction.size() > 0) {
        def anyoneHome = presenceRestriction.any { it.currentValue("presence") == "present" }
        if (!anyoneHome) {
            logDebug "No one home based on selected presence sensors"
            return false
        }
    }

    // Check illuminance restrictions
    if (illuminanceRestriction && illuminanceThreshold != null) {
        def currentLux = illuminanceRestriction.currentValue("illuminance")
        if (currentLux != null && currentLux > illuminanceThreshold) {
            logDebug "Room too bright: ${currentLux} lux > ${illuminanceThreshold} lux threshold"
            return false
        }
    }

    logDebug "All trigger conditions met."
    return true
}

// Starts the alert sequence
def startAlert(reason, emergency = false) {
    logDebug "startAlert called with reason: ${reason}"

    // If an alert is already running, stop it immediately before starting new one
    if (atomicState.isAlertRunning) {
        log.info "Alert already running, stopping it before starting new one: ${reason}"
        // Pass a specific reason to avoid double "end" notifications if desired
        stopAlertImmediate(reason: "new alert triggered", sendNotify: false)
        pauseExecution(1500) // Give time for stop/restore to settle
    }

    // Check conditions *after* potentially stopping previous alert
    if (!isValidTrigger()) {
        logDebug "Conditions not met, not starting alert."
        return
    }

    log.info "Starting alert: ${reason}" + (state.lastTriggerDevice ? " (triggered by device ID: ${state.lastTriggerDevice})" : "")

    // --- Set Initial Alert State ---
    atomicState.isAlertRunning = true
    state.alertStart = now()
    state.alertReason = reason
    unschedule() // Clear any previous timeout or repeat schedules

    // Save current device states if restoration is enabled (using value stored in state)
    logDebug "startAlert: Checking restorePreviousEnabled state value: ${state.restorePreviousEnabled}"
    if (state.restorePreviousEnabled) {
        saveDeviceStates() // Call directly, now saves JSON to atomicState.previousStatesJson
    } else {
        logDebug "startAlert: Not saving device states as restorePreviousEnabled is false/null."
        atomicState.previousStatesJson = null // Clear atomic state JSON string if not restoring
    }

    // --- Execute Pattern ---
    // This needs to happen *after* state is set and devices saved
    def patternInfo = getPatternInfo(emergency ? "Emergency" : null)
    // No need to pass restorePrevious in patternInfo anymore

    logDebug "Running pattern: ${patternInfo.type}"
    // Store consolidated pattern parameters in a single atomicState key
    def patternParamsMap = [
        commands: patternInfo.commands ?: [],
        repeatCount: patternInfo.repeatCount,
        isInfinite: patternInfo.isInfinite,
        colorMap: patternInfo.colorMap,
        level: patternInfo.level,
        offLevel: patternInfo.offLevel,
        patternType: patternInfo.type
    ]
    String patternParamsJson = JsonOutput.toJson(patternParamsMap)
    atomicState.currentPatternParamsJson = patternParamsJson
    logDebug "Stored consolidated pattern data in atomicState.currentPatternParamsJson"

    // --- Schedule First Pattern Step ---
    long startDelayMs = 200 // Short delay before starting processing
    logDebug "Scheduling first processPatternStep in ${startDelayMs} ms"
    // Schedule the first step (index 0, loop 0)
    runInMillis(startDelayMs, "processPatternStep", [data: [stepIndex: 0, loopCounter: 0], overwrite: false])

    // --- Notifications & Schedule Final Cleanup ---
    if (notifyStart) {
        sendNotification("VisualAlert: ${alertName} activated - ${reason}")
    }

    // Calculate total duration for finite patterns or use timeout for infinite (PARALLEL execution)
    def cleanupDelaySeconds
    if (patternInfo.isInfinite) {
        def effectiveTimeoutMinutes = timeout ?: 5 // Default to 5 minutes if not set
        if (effectiveTimeoutMinutes > 0) {
            cleanupDelaySeconds = effectiveTimeoutMinutes * 60
            logDebug "Pattern is indefinite, scheduling final cleanup/restore in ${cleanupDelaySeconds} seconds (timeout)."
        } else {
            cleanupDelaySeconds = -1 // Indicates no scheduled cleanup for infinite without timeout
            logDebug "Pattern is indefinite and timeout is 0, alert will run until stopped manually. No cleanup scheduled."
        }
    } else {
        // Finite patterns restore immediately when the last step executes; this schedule
        // is only a failsafe in case the step chain dies midway (error, hub restart).
        // Allow generous per-step overhead so it never fires before a healthy pattern finishes.
        long loopDurationMs = patternInfo.commands.sum { it.duration ?: 0 }
        long totalPatternMs = loopDurationMs * patternInfo.repeatCount
        int totalSteps = patternInfo.commands.size() * patternInfo.repeatCount
        long bufferMs = 10000 + (totalSteps * 500)
        cleanupDelaySeconds = ((totalPatternMs + bufferMs) / 1000)
        logDebug "Pattern is finite (${patternInfo.repeatCount} repeats). Scheduling failsafe cleanup/restore in ${cleanupDelaySeconds} seconds."
    }

    // Schedule the cleanup if a delay was determined (timeout for infinite, failsafe for finite)
    if (cleanupDelaySeconds > 0) {
        logDebug "Scheduling finalCleanupAndRestore in ${cleanupDelaySeconds} seconds. Reason: ${patternInfo.isInfinite ? 'timeout' : 'pattern completed'}"
        runIn(cleanupDelaySeconds.toInteger(), "finalCleanupAndRestore", [data: [reason: patternInfo.isInfinite ? "timeout" : "pattern completed"], overwrite: true])
    }
}

// Central function for cleanup and restoration, called by runIn or stop handlers
def finalCleanupAndRestore(data) {
    def reason = data?.reason ?: "unknown completion"
    log.info "Final cleanup and restore initiated due to: ${reason}"

    // Check if alert is still considered running (might have been stopped manually already)
    // Note: isAlertRunning might be false if called directly from stopAlertImmediate
    // We proceed anyway to ensure restoration happens after an immediate stop.
    if (!atomicState.isAlertRunning && reason == "pattern completed") { // Only skip if already stopped AND it was a normal completion
        logDebug "Alert already stopped (likely via immediate stop), ignoring scheduled finalCleanupAndRestore call for 'pattern completed'."
        unschedule("finalCleanupAndRestore")
        return
    }

    // --- Stop Pattern & Restore State ---
    // Set running flag false FIRST to signal loops (if not already false from immediate stop)
    if (atomicState.isAlertRunning) {
        atomicState.isAlertRunning = false
        logDebug "finalCleanupAndRestore: Set atomicState.isAlertRunning to false"
    }
    unschedule("processPatternStep") // Cancel any pending steps

    // --- Conditionally Force ON for Immediate Stops ---
    def immediateStopReasons = ["manual stop", "Stop Test button pressed", "Stop button pressed", "disabled by switch", "new alert triggered", "trigger reversed"]
    boolean isImmediateStop = immediateStopReasons.any { reason.contains(it) }

    if (isImmediateStop) {
        logDebug("finalCleanupAndRestore: Immediate stop detected ('${reason}'). Forcing devices ON before restore.")
        devices?.on()
        pauseExecution(200) // Shorter pause just in case
    }
    // --- End Conditional Force ON ---


    boolean shouldRestore = state.restorePreviousEnabled ?: false
    logDebug "finalCleanupAndRestore: Checking restorePreviousEnabled state value: ${shouldRestore}"

    if (shouldRestore) {
        String previousStatesJson = atomicState.previousStatesJson
        if (previousStatesJson) {
            logDebug("finalCleanupAndRestore: Found previousStatesJson: ${previousStatesJson}")
            try {
                Map statesToRestoreMap = new JsonSlurper().parseText(previousStatesJson)
                if (statesToRestoreMap && !statesToRestoreMap.isEmpty()) {
                    logDebug("finalCleanupAndRestore: Successfully parsed JSON. Calling restoreDeviceStates.")
                    restoreDeviceStates(statesToRestoreMap) // Restore from JSON map
                } else {
                    log.warn("finalCleanupAndRestore: Parsed JSON map is empty or null. Turning devices off.")
                    devices?.off()
                    atomicState.previousStatesJson = null // Clear invalid state
                }
            } catch (Exception e) {
                log.error("finalCleanupAndRestore: Error parsing previousStatesJson: ${e.message}. Turning devices off.")
                devices?.off()
                atomicState.previousStatesJson = null // Clear invalid state
            }
        } else {
            logDebug("finalCleanupAndRestore: atomicState.previousStatesJson is empty/null. Turning devices off.")
            devices?.off()
        }
    } else {
        // --- UPDATED: Always turn off if restore is disabled ---
        logDebug "finalCleanupAndRestore: Restore disabled, turning devices off."
        devices?.off()
        // --- END UPDATE ---
    }

    // --- Notifications ---
    // Send notification only if it wasn't triggered by a new alert starting
    if (notifyEnd && reason != "new alert triggered") {
        // Use the original alert reason if available, otherwise the cleanup reason
        def notifyReason = state.alertReason ?: reason
        sendNotification("VisualAlert: ${alertName} deactivated - ${notifyReason}")
    }

    // --- Final State Cleanup ---
    logDebug "Final cleanup: Clearing state variables."
    // When a new alert preempts this one, the handler has already stored the new
    // trigger device in lastTriggerDevice - don't wipe it
    if (reason != "new alert triggered") {
        state.lastTriggerDevice = null
    }
    state.alertStart = null
    state.alertReason = null
    atomicState.previousStatesJson = null // Ensure JSON is cleared after use/attempt
    atomicState.currentPatternParamsJson = null // Clean up pattern params
}

// Stops the alert immediately (e.g., manual stop button, new alert starting)
def stopAlertImmediate(evtOrParams = null) {
    // Determine if called by runIn (evtOrParams will have a 'data' key) or directly
    def params = evtOrParams instanceof Map ? evtOrParams : [:]
    def data = evtOrParams?.data instanceof Map ? evtOrParams.data : [:]

    def reason = data.reason ?: params.reason ?: "manual stop"

    log.info "Stopping alert immediately (stopAlertImmediate): ${reason}"

    // Check if alert is actually running
    if (!atomicState.isAlertRunning) {
        logDebug "Alert not running when stopAlertImmediate called, ignoring."
        // Ensure any lingering cleanup schedule is cancelled
        unschedule("finalCleanupAndRestore")
        return
    }

    // Signal loops to stop by setting the central flag FIRST
    atomicState.isAlertRunning = false
    logDebug "stopAlertImmediate: Set atomicState.isAlertRunning to false"
    unschedule("processPatternStep") // Cancel any pending steps FIRST
    logDebug "stopAlertImmediate: Unscheduled any pending processPatternStep."

    // Cancel any scheduled final cleanup
    unschedule("finalCleanupAndRestore")
    logDebug "stopAlertImmediate: Unscheduled any pending finalCleanupAndRestore."

    // Call the central cleanup function immediately
    // Pass the reason, which will now be checked inside finalCleanupAndRestore
    finalCleanupAndRestore([reason: reason])
}


// --- State Management ---

// Saves current device states before starting an alert and stores as JSON in atomicState
void saveDeviceStates() {
    log.info "Saving device states before alert"
    Map tempStates = [:] // Use a temporary map
    atomicState.previousStatesJson = null // Clear previous JSON string *before* loop

    settings.devices.each { device -> // Explicitly use settings.devices
        try {
            def currentLevel = hasLevelCapability(device) ? device.currentValue("level") : null
            def currentHue = hasColorCapability(device) ? device.currentValue("hue") : null
            def currentSat = hasColorCapability(device) ? device.currentValue("saturation") : null
            def currentCT = hasColorTemperature(device) ? device.currentValue("colorTemperature") : null

            // --- UPDATED Switch State Logic ---
            // Determine switch state directly first
            def currentSwitch = device.currentValue("switch")
            logDebug "Device ${device.displayName}: Reported switch state: '${currentSwitch}'"

            // Fallback for invalid reported switch state
            if (currentSwitch != "on" && currentSwitch != "off") {
                logWarn "Device ${device.displayName} reported invalid switch state '${currentSwitch}'. Attempting fallback using level."
                // Fallback: Check level only if switch state is invalid
                if (hasLevelCapability(device) && currentLevel != null && currentLevel > 0) {
                     currentSwitch = "on"
                     logWarn "Fallback: Using level ${currentLevel} > 0, assuming switch state is 'on'."
                } else {
                     currentSwitch = "off" // Final fallback
                     logWarn "Fallback: Assuming switch state is 'off'."
                }
            }
            // --- END UPDATED Switch State Logic ---

            def stateObj = [
                switch: currentSwitch,
                level: currentLevel,
                hue: currentHue,
                saturation: currentSat,
                colorTemperature: currentCT
            ]

            tempStates[device.id.toString()] = stateObj // Save to temporary map
            logDebug "Saved initial state for ${device.displayName} to temp map: ${stateObj}"
        } catch (e) {
            log.error "Error saving state for ${device.displayName}: ${e.message}"
        }
    }
    try {
        atomicState.previousStatesJson = JsonOutput.toJson(tempStates) // Convert map to JSON and store in atomicState
        logDebug "Finished saving device states as JSON to atomicState.previousStatesJson. Count: ${tempStates?.size() ?: 0}. JSON: ${atomicState.previousStatesJson}"
    } catch (Exception e) {
        log.error "Error converting device states map to JSON: ${e.message}"
        atomicState.previousStatesJson = null // Ensure it's null on error
    }
}

// Restores device states after an alert stops
def restoreDeviceStates(Map statesToRestore) { // Now requires the map to restore from
    // No longer need defensive copy as we get a new map from JSON parsing
    if (!statesToRestore || statesToRestore.isEmpty()) {
        log.warn "restoreDeviceStates called but no states found to restore (or map was empty). Turning devices off."
        devices?.off()
        // Clear atomicState JSON even if restore failed/wasn't needed
        atomicState.previousStatesJson = null
        return
    }

    log.info "Restoring device states. Count: ${statesToRestore?.size() ?: 0}. States: ${statesToRestore}"

    // --- REMOVED pre-restore OFF loop ---
    // settings.devices?.each { device -> // Explicitly use settings.devices
    //     try {
    //         device.off()
    //     } catch (Exception e) {
    //         log.warn "Error turning off ${device.displayName} during pre-restore: ${e.message}"
    //     }
    // }
    // pauseExecution(1000) // Give devices time to settle
    // --- END REMOVAL ---

    // Now restore previous states
    settings.devices.each { device -> // Explicitly use settings.devices
        def deviceIdStr = device.id.toString()
        def prevState = statesToRestore[deviceIdStr] // Use the map directly

        if (prevState) {
            logDebug "Attempting to restore ${device.displayName} to previous state: ${prevState}"
            try {
                boolean shouldBeOn = (prevState.switch == "on")
                logDebug "Device ${device.displayName}: Saved state indicates shouldBeOn=${shouldBeOn}"

                if (shouldBeOn) {
                    // Restore attributes BEFORE turning on
                    restoreDeviceAttributes(device, prevState)
                    pauseExecution(500) // Wait for attributes to potentially take effect

                    // Turn ON
                    device.on()
                    logDebug "Restored ${device.displayName} to ON"

                    // Verification step: retry up to the configured Retry Attempts if the device
                    // doesn't report ON (helpful for slow/problematic Z-Wave devices)
                    int maxRetries = (settings.retryCount != null) ? (settings.retryCount as Integer) : 3
                    for (int attempt = 1; attempt <= maxRetries; attempt++) {
                        pauseExecution(1000)
                        if (device.currentValue("switch") == "on") {
                            break
                        }
                        log.warn "Device ${device.displayName} did not report ON after restore (current: ${device.currentValue('switch')}), retry ${attempt}/${maxRetries}."
                        device.on()
                    }
                } else {
                    // --- ADDED: Restore attributes even if turning off ---
                    logDebug "Device ${device.displayName} should be OFF. Restoring attributes first."
                    restoreDeviceAttributes(device, prevState)
                    pauseExecution(500) // Wait for attributes to potentially take effect
                    // --- END ADD ---

                    // Ensure device is OFF
                    device.off()
                    logDebug "Ensured ${device.displayName} is OFF (as per saved state)"
                }
            } catch (Exception e) {
                log.error "Error restoring state for ${device.displayName}: ${e.message}"
                // Fallback: try to ensure it's at least off if restoration failed badly
                try { device.off() } catch (e2) {}
            }
        } else {
            log.warn "No saved state found for ${device.displayName} in restore map. Ensuring device is off."
            try { device.off() } catch (e) {}
        }
    }

    // Clear atomicState JSON after attempting restore
    atomicState.previousStatesJson = null
    logDebug "Finished restoring device states."

    // Schedule a final check (optional, good for Z-Wave) - Commented out as it causes errors and restore seems to work
    // Commented-out finalStateCheck call is removed
}

// Helper to restore specific attributes (color, level, CT)
private void restoreDeviceAttributes(device, Map attrs) {
    logDebug "Restoring attributes for ${device.displayName}: ${attrs}"
    // Prioritize Color Temp -> Color -> Level
    try {
        if (attrs.colorTemperature != null && hasColorTemperature(device)) {
            device.setColorTemperature(attrs.colorTemperature)
            logDebug "Restored Color Temperature: ${attrs.colorTemperature}"
            pauseExecution(300)
        }
        // Restore Hue/Saturation if available (Remove 'else' to allow both CT and Color restore if needed)
        if (attrs.hue != null && attrs.saturation != null && hasColorCapability(device)) {
            // Use setColor map for reliability if possible
            try {
                def colorMap = [hue: attrs.hue, saturation: attrs.saturation]
                // Include level only if it's part of the color command for this device type
                // if (device.hasCommand("setColor", [Map])) { // Check if setColor accepts level
                //    if (attrs.level != null) colorMap.level = attrs.level
                // }
                device.setColor(colorMap)
                logDebug "Restored Color (Hue/Sat) using setColor: ${colorMap}"
                pauseExecution(300)
            } catch (e) {
                logWarn "setColor failed for ${device.displayName}, trying individual Hue/Sat: ${e.message}"
                // Fallback to individual commands
                device.setHue(attrs.hue)
                pauseExecution(200)
                device.setSaturation(attrs.saturation)
                logDebug "Restored Color using individual setHue/setSaturation"
                pauseExecution(300)
            }
        }

        // Restore Level (do this after color/CT)
        if (attrs.level != null && hasLevelCapability(device)) {
            // Avoid setting level if setColor already handled it (might cause issues)
            // This requires knowing if device's setColor includes level, which is tricky.
            // Safest is often to set it separately.
            device.setLevel(attrs.level)
            logDebug "Restored Level: ${attrs.level}"
            pauseExecution(300)
        }
    } catch (Exception e) {
        log.warn "Error restoring attributes for ${device.displayName}: ${e.message}"
    }
}


// --- Pattern Execution ---

// Determines pattern details based on settings
def getPatternInfo(String type = null) {
    def patternTypeToUse = type ?: settings.patternType ?: "Simple Flash" // Use settings.patternType
    // Use the value from the input field directly. Default to 1 if null or invalid.
    def repeatCountInput = settings.repeatCount != null ? settings.repeatCount.toInteger() : 5 // Default to 5 if setting is null
    // Determine if infinite based on repeatCount being 0
    def isInfinite = (repeatCountInput == 0)
    // Use 1 for loop checks if infinite, otherwise use the input value
    def actualRepeatCount = isInfinite ? 1 : repeatCountInput

    def info = [
        type: patternTypeToUse,
        isInfinite: isInfinite,
        repeatCount: actualRepeatCount, // Store the count for executeDevicePattern loop
        commands: []
    ]

    switch(patternTypeToUse) {
        case "Doorbell":
            info.commands = [
                [on: true, duration: 300], [on: false, duration: 300], // Quick flash 1
                [on: true, duration: 300], [on: false, duration: 1000]  // Quick flash 2, pause
            ]
            // info.repeatCount is set above from input
            break
        case "Emergency":
            info.commands = [
                [on: true, duration: 300], [on: false, duration: 300]
            ]
            // info.repeatCount is set above from input
            info.level = 100 // Force full brightness
            break
        case "Strobe":
            info.commands = [
                [on: true, duration: 200], [on: false, duration: 200],
                [on: true, duration: 200], [on: false, duration: 200],
                [on: true, duration: 200], [on: false, duration: 1500] // Longer pause
            ]
            // info.repeatCount is set above from input
            break
        case "Standby":
             info.commands = [
                [on: true, duration: 3000], // On for 3 seconds
                [on: false, duration: 3000] // Off for 3 seconds
            ]
            // info.repeatCount is set above from input
            break
        case "Custom":
            info.commands = [] // Initialize empty command list
            def sequenceString = settings.customPatternSequence ?: "ON:1000,OFF:1000" // Get sequence from settings, default if null/empty
            logDebug "Parsing custom sequence: ${sequenceString}"
            try {
                sequenceString.split(',').each { step ->
                    step = step.trim() // Remove leading/trailing whitespace
                    if (step) { // Ensure step is not empty after trimming
                        def parts = step.split(':')
                        if (parts.size() == 2) {
                            def stateStr = parts[0].trim().toUpperCase()
                            def durationStr = parts[1].trim()
                            boolean onState = (stateStr == "ON")
                            long durationMs = durationStr.toLong() // Convert duration to long

                            if (durationMs <= 0) {
                                log.warn "Custom pattern step '${step}' has invalid duration (${durationMs}ms). Skipping."
                            } else if (stateStr != "ON" && stateStr != "OFF") {
                                log.warn "Custom pattern step '${step}' has invalid state ('${parts[0]}'). Skipping."
                            } else {
                                info.commands << [on: onState, duration: durationMs]
                            }
                        } else {
                            log.warn "Custom pattern step '${step}' has incorrect format (expected STATE:ms). Skipping."
                        }
                    }
                }
            } catch (NumberFormatException e) {
                log.error "Error parsing custom pattern duration in sequence '${sequenceString}': ${e.message}. Defaulting pattern."
                info.commands = [[on: true, duration: 1000], [on: false, duration: 1000]] // Default on error
            } catch (Exception e) {
                log.error "Error parsing custom pattern sequence '${sequenceString}': ${e.message}. Defaulting pattern."
                info.commands = [[on: true, duration: 1000], [on: false, duration: 1000]] // Default on error
            }

            // Ensure commands list is not empty after parsing
            if (info.commands.isEmpty()) {
                log.warn "Custom pattern sequence '${sequenceString}' resulted in empty command list. Defaulting pattern."
                info.commands = [[on: true, duration: 1000], [on: false, duration: 1000]]
            }
            // info.repeatCount and info.isInfinite are set above from input
            break
        case "Simple Flash":
        default:
            info.commands = [
                [on: true, duration: 1000], [on: false, duration: 1000]
            ]
            // info.repeatCount is set above from input
            break
    }

    // Add common properties (color/level) if not emergency
    if (patternTypeToUse != "Emergency") {
        if (settings.useColor && settings.alertColor) { // Use settings.useColor etc.
            // Convert hex color to HSL map using the alertLevel as the target level
            info.colorMap = hexToHslMap(settings.alertColor, (settings.alertLevel ?: 100) as Integer)
            // Keep original hex for logging if needed, but colorMap is used for commands
            info.colorHex = settings.alertColor
        }
        if (settings.alertLevel != null) { // Use settings.alertLevel
            info.level = settings.alertLevel
        }
    }
    // Add offLevel from settings
    info.offLevel = settings.offLevel ?: 0 // Use settings.offLevel

    logDebug "Generated pattern info: ${info}" // Keep log for debugging
    return info
}

// Handles one step of the pattern for ALL devices
def processPatternStep(data) {
    int stepIndex = data.stepIndex ?: 0
    int loopCounter = data.loopCounter ?: 0

    // Check if alert is still running
    if (!atomicState.isAlertRunning) {
        logDebug "processPatternStep: Alert stopped. Exiting step ${stepIndex}, loop ${loopCounter}."
        atomicState.remove("currentPatternParamsJson") // Clean up if stopped mid-pattern
        return
    }

    // Retrieve pattern data from atomicState
    String patternParamsJson = atomicState.currentPatternParamsJson
    if (!patternParamsJson) {
        log.error "processPatternStep: Could not find pattern data in atomicState.currentPatternParamsJson. Stopping alert."
        stopAlertImmediate(reason: "Pattern data missing")
        return
    }

    // Parse the consolidated JSON payload
    def jsonSlurper = new JsonSlurper()
    Map patternParams = [:]
    try {
        patternParams = jsonSlurper.parseText(patternParamsJson)
    } catch (Exception e) {
        log.error "Error parsing patternParamsJson from atomicState in processPatternStep: ${e.message}"
        log.error "--> JSON was: ${patternParamsJson}"
        stopAlertImmediate(reason: "Error parsing pattern data")
        return
    }

    // Access parameters from the parsed map
    List commands = patternParams.commands ?: []
    Map colorMap = patternParams.colorMap // May be null
    def repeatCount = patternParams.repeatCount
    def isInfinite = patternParams.isInfinite
    def level = patternParams.level
    def offLevel = patternParams.offLevel
    def patternType = patternParams.patternType

    // Basic validation
    if (commands.isEmpty() || repeatCount == null || isInfinite == null || patternType == null) {
        log.error "processPatternStep: Missing essential parameters after parsing JSON. Parsed map: ${patternParams}. Stopping alert."
        stopAlertImmediate(reason: "Invalid pattern data")
        return
    }

    // Check if pattern loops are finished (for finite patterns)
    if (!isInfinite && loopCounter >= repeatCount) {
        logDebug "processPatternStep: Finite pattern completed (${loopCounter}/${repeatCount} loops). No more steps."
        // Final cleanup is handled by the scheduled finalCleanupAndRestore
        return
    }

    // Get the command for the current step index
    if (stepIndex >= commands.size()) {
        log.error "processPatternStep: stepIndex ${stepIndex} is out of bounds for commands list size ${commands.size()}. This shouldn't happen. Stopping alert."
        stopAlertImmediate(reason: "Pattern step index error")
        return
    }
    Map currentCommand = commands[stepIndex]
    boolean turnOn = currentCommand.on
    long duration = currentCommand.duration ?: 1000 // Default duration if missing

    logDebug "processPatternStep: Loop ${loopCounter + 1}/${isInfinite ? 'Inf' : repeatCount}, Step ${stepIndex + 1}/${commands.size()}. Action: ${turnOn ? 'ON' : 'OFF'}, Duration: ${duration}ms"

    // --- Apply command to ALL devices ---
    settings.devices.each { device -> // Explicitly use settings.devices
        try {
            if (turnOn) {
                def targetLevel = level ?: 100
                // Apply color FIRST if specified
                if (colorMap && hasColorCapability(device)) {
                    try {
                        // logDebug "Setting color map for ${device.displayName}: ${colorMap}" // Reduce noise
                        device.setColor(colorMap)
                    } catch (e) { log.warn "Error setting color map for ${device.displayName}: ${e.message}" }
                }
                // Apply level SECOND
                if (hasLevelCapability(device)) {
                     try {
                        // logDebug "Setting level for ${device.displayName}: ${targetLevel}" // Reduce noise
                        device.setLevel(targetLevel)
                     } catch (e) { log.warn "Error setting level for ${device.displayName}: ${e.message}" }
                }
                // Ensure device is ON THIRD (after color/level)
                // logDebug "Turning ON ${device.displayName}" // Reduce noise
                device.on()
                // logDebug "${device.displayName} is ON" // Reduce noise

            } else { // OFF state logic
                def targetOffLevel = offLevel ?: 0
                if (targetOffLevel > 0 && hasLevelCapability(device)) {
                    // logDebug "Setting OFF level for ${device.displayName}: ${targetOffLevel}" // Reduce noise
                    device.setLevel(targetOffLevel)
                    // Ensure the device is ON to maintain the dim level
                    if (device.currentValue("switch") != "on") {
                         // logDebug "Turning ON ${device.displayName} to maintain OFF level" // Reduce noise
                         device.on()
                    }
                    // logDebug "${device.displayName} set to OFF Level ${targetOffLevel}" // Reduce noise
                } else {
                    // Standard OFF behavior
                    // logDebug "Turning OFF ${device.displayName}" // Reduce noise
                    device.off()
                    // logDebug "${device.displayName} is OFF" // Reduce noise
                }
            }
        } catch (Exception e) {
            log.error "Error executing step ${stepIndex} on ${device.displayName}: ${e.message}"
            // Continue to next device even if one fails
        }
    } // End devices.each

    // --- Check if this was the absolute last step ---
    boolean isLastStepOfLastLoop = !isInfinite && loopCounter == (repeatCount - 1) && stepIndex == (commands.size() - 1)

    // --- Schedule the next step OR Force ON if last step ---
    int nextStepIndex = stepIndex + 1
    int nextLoopCounter = loopCounter

    // Check if we finished the command list for this loop
    if (nextStepIndex >= commands.size()) {
        nextStepIndex = 0 // Reset to first step
        nextLoopCounter++ // Increment loop counter
    }

    // Check again if alert is running before scheduling/forcing ON
    if (!atomicState.isAlertRunning) {
        logDebug "processPatternStep: Alert stopped after processing step ${stepIndex}. Not scheduling next step or forcing ON."
        return
    }

    if (isLastStepOfLastLoop) {
        // Final step of a finite pattern: restore devices right now instead of waiting
        // for the scheduled failsafe. Cancel the failsafe since we no longer need it.
        logDebug "processPatternStep: Executed final step of finite pattern. Restoring devices now."
        unschedule("finalCleanupAndRestore")
        finalCleanupAndRestore([reason: "pattern completed"])
    } else if (isInfinite || nextLoopCounter < repeatCount) {
        // Schedule the next step if the pattern should continue
        logDebug "processPatternStep: Scheduling next step (Index: ${nextStepIndex}, Loop: ${nextLoopCounter}) in ${duration}ms"
        runInMillis(duration, "processPatternStep", [data: [stepIndex: nextStepIndex, loopCounter: nextLoopCounter], overwrite: false]) // Use overwrite: false to allow multiple steps pending
    } else {
        // This case should technically not be reached if isLastStepOfLastLoop is handled above, but kept as safety
        logDebug "processPatternStep: Finished last loop (${nextLoopCounter}/${repeatCount}). No more steps to schedule."
        // Final cleanup is handled by the scheduled finalCleanupAndRestore
    }
}


// --- Utility Methods ---

// Convert Hex Color String (#RRGGBB) to Hubitat HSL Map [hue, saturation, level]
private Map hexToHslMap(String hexColor, Integer level = 100) {
    if (!hexColor || !hexColor.matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})\$")) {
        log.warn "Invalid hex color format received: ${hexColor}. Defaulting to White."
        // Default to White (Hue 0, Sat 0, Level 100) for Hubitat
        return [hue: 0, saturation: 0, level: level ?: 100]
    }

    try {
        // Remove #
        String hex = hexColor.substring(1)
        // Handle shorthand hex (e.g., #RGB)
        if (hex.length() == 3) {
            hex = hex.collect { it + it }.join()
        }

        // Convert hex to RGB (0-255)
        Integer r = Integer.parseInt(hex.substring(0, 2), 16)
        Integer g = Integer.parseInt(hex.substring(2, 4), 16)
        Integer b = Integer.parseInt(hex.substring(4, 6), 16)

        // Convert RGB to HSL (formulas adapted for Groovy)
        BigDecimal r_ = r / 255.0
        BigDecimal g_ = g / 255.0
        BigDecimal b_ = b / 255.0

        BigDecimal cmax = [r_, g_, b_].max()
        BigDecimal cmin = [r_, g_, b_].min()
        BigDecimal delta = cmax - cmin

        BigDecimal hue = 0
        BigDecimal saturation = 0
        BigDecimal lightness = (cmax + cmin) / 2.0

        if (delta != 0) {
            BigDecimal satDenominator = (1.0 - (2.0 * lightness - 1.0).abs()) // Keep denominator check
            if (satDenominator == 0) {
                logWarn "Saturation denominator is zero, setting saturation to 0."
                saturation = 0
            } else {
                saturation = delta / satDenominator
            }

            // Hue calculation (already guarded by delta != 0)
            if (cmax == r_) {
                hue = 60.0 * (((g_ - b_) / delta).remainder(6.0)) // Use remainder for BigDecimal
            } else if (cmax == g_) {
                hue = 60.0 * (((b_ - r_) / delta) + 2.0)
            } else { // cmax == b_
                hue = 60.0 * (((r_ - g_) / delta) + 4.0) // No modulo needed here based on standard HSL conversion
            }
        } else {
            // If delta is 0, hue and saturation are 0 (it's a gray color)
            hue = 0
            saturation = 0
        }

        if (hue < 0) {
            hue += 360.0
        }

        // Scale to Hubitat's 0-100 range
        // Add checks before rounding/casting
        if (hue == null || saturation == null) {
            log.error "Hue or Saturation became null during calculation!"
            return [hue: 0, saturation: 0, level: level ?: 100] // Default to white
        }

        // Scale hue
        BigDecimal scaledHue = (hue / 360.0) * 100.0
        Integer hubHue = 0 // Default value
        if (scaledHue != null) {
            try {
                hubHue = scaledHue.intValue() // Use intValue() instead of round() as Integer
            } catch (NullPointerException npe) {
                log.error "NPE during hue rounding/casting: ${npe.message}. ScaledHue was: ${scaledHue}"
                // Keep hubHue as 0
            } catch (Exception e) { // Corrected line number based on previous diff
                 log.error "Error during hue rounding/casting: [${e.class.name}] ${e.message}. ScaledHue was: ${scaledHue}"
                 // Keep hubHue as 0
            }
        } else {
             log.error "scaledHue was null before rounding!"
        }

        // Scale saturation
        BigDecimal scaledSaturation = saturation * 100.0
        Integer hubSaturation = 0 // Default value
        if (scaledSaturation != null) {
             try {
                hubSaturation = scaledSaturation.intValue() // Use intValue() instead of round() as Integer
             } catch (NullPointerException npe) { // Corrected line number based on previous diff
                log.error "NPE during saturation rounding/casting: ${npe.message}. ScaledSaturation was: ${scaledSaturation}"
                // Keep hubSaturation as 0
             } catch (Exception e) { // Corrected line number based on previous diff
                 log.error "Error during saturation rounding/casting: [${e.class.name}] ${e.message}. ScaledSaturation was: ${scaledSaturation}"
                 // Keep hubSaturation as 0
             }
        } else {
             log.error "scaledSaturation was null before rounding!"
        }

        // Return the map
        return [hue: hubHue, saturation: hubSaturation, level: level ?: 100]

    } catch (Exception e) {
        log.error "Error converting hex color ${hexColor} to HSL map: ${e.message}"
        return [hue: 0, saturation: 0, level: level ?: 100] // Default to white on any error
    }
}

// Checks if a device has the ColorControl capability
private boolean hasColorCapability(device) {
    return device.hasCapability("ColorControl")
}

// Checks if a device has the ColorTemperature capability
private boolean hasColorTemperature(device) {
    return device.hasCapability("ColorTemperature")
}

// Checks if a device has the SwitchLevel capability
private boolean hasLevelCapability(device) {
    return device.hasCapability("SwitchLevel")
}

// Send notification to selected devices and/or parent app
def sendNotification(String message) {
    if (notificationDevices) {
        logDebug "Sending notification to devices: ${message}"
        notificationDevices.deviceNotification(message)
    }
    // Route through the parent app, which honors the global notification setting
    try {
        parent?.sendChildNotification(message)
        logDebug "Sent notification to parent app: ${message}"
    } catch (Exception e) {
        log.warn "Could not send notification to parent app: ${e.message}"
    }
}

// Logging helper
void logDebug(String msg) {
    if (enableLogging) {
        log.debug msg
    }
}

// Logging helper for warnings
void logWarn(String msg) {
    log.warn msg // Warnings are always logged
}

// Helper to format section headers
private String getFormat(type, msg) {
    if (type == "header-blue") { // Example type
        msg = "<div style='color:#66ccff;font-weight:bold'>${msg}</div>"
    }
    return msg
}

// Helper to get image path (replace with actual paths if needed)
private String getImage(img) {
    // return "<img src='https://raw.githubusercontent.com/bptworld/Hubitat/master/resources/images/${img}.png' width='30' height='30'></img>"
    return "" // Placeholder if images aren't used
}

// Helper to display common elements (like title)
private void display() {
    setVersion()
}

// Sets the version number in state
private void setVersion(){
    state.version = "1.3.0"
}

// Provides the title for display pages
String title() {
    def txt = "<div style='color:#66ccff;font-weight:bold;font-size:20px;text-align:center'>VisualAlert Child (${state.version})</div>"
    return txt
}