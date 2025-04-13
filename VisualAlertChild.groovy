/**
 *  VisualAlert Child
 *
 *  Advanced alert configuration for the VisualAlert system
 *  Manages individual alert patterns and triggers
 *
 *  Copyright 2024
 *  Licensed under the Apache License, Version 2.0
 *
 *  Version: 1.1.5
 */

definition(
    name: "VisualAlert Child",
    namespace: "TechBill",
    author: "Bill Fleming",
    description: "Configure individual visual alert patterns and triggers",
    parent: "TechBill:VisualAlert",
    category: "Convenience",
    iconUrl: "http://cdn.device-icons.smartthings.com/Lighting/light11-icn.png",
    iconX2Url: "http://cdn.device-icons.smartthings.com/Lighting/light11-icn@2x.png",
    iconX3Url: "http://cdn.device-icons.smartthings.com/Lighting/light11-icn@3x.png"
)

preferences {
    page(name: "mainPage")
    page(name: "patternPage")
    page(name: "schedulePage")
    page(name: "advancedPage")
}

def mainPage() {
    dynamicPage(name: "mainPage", title: "", install: true, uninstall: true, nextPage: null) { // Removed title
        display() // Added display call
        section(getFormat("header-blue", "${getImage("Blank")}" + "Alert Name & Devices")) {
            input "alertName", "text",
                title: "VisualAlert Child Name", // Changed label
                required: true,
                submitOnChange: true

            input "devices", "capability.switch",
                title: "Select Devices",
                multiple: true,
                required: true,
                description: "Works with any switchable device: smart bulbs, Zigbee/Z-Wave switches, dimmers, outlets, etc."
        }

        section(getFormat("header-blue", "${getImage("Blank")}" + "Trigger Sources")) {
            paragraph "Select switches, buttons, or sensors (Contact, Motion, Smoke, CO, Water) that will activate this alert" // Updated paragraph

            input "triggerSwitches", "capability.switch",
                title: "Switches",
                multiple: true,
                required: false,
                submitOnChange: true,
                description: "Select switches that can trigger this alert" // Updated description
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

                // Display selected buttons (Moved inside the 'if (buttonOptions)' block)
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

// Removed leftover comment
// Removed duplicate comment
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

            // Removed erroneous leftover code block
        }

        section(getFormat("header-blue", "${getImage("Blank")}" + "Stop Alert | Enable/Disable VisualAlert Trigger")) { // Changed title
            paragraph "Optional: Select buttons that will stop this alert when pressed"

            input "stopButtonDevices", "capability.pushableButton",
                title: "Select Button(s)",
                multiple: true,
                required: false,
                submitOnChange: true,
                description: "Choose button devices that can stop this alert"

            paragraph "Optional: Select switch that will enable/disable this child app." // Added description for disable switch
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

        // Test Alert section moved below

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

// Helper function to get pattern description (Moved before patternPage for clarity)
String getPatternDescription(String patternType) {
    switch (patternType) {
        case "Simple Flash":
            return "<b>Simple Flash:</b> A single flash on, then off. Repeats based on 'Number of Repeats'."
        case "Doorbell":
            return "<b>Doorbell:</b> Two quick flashes followed by a pause. Repeats based on 'Number of Repeats'."
        case "Emergency":
            return "<b>Emergency:</b> Rapid flashing sequence. Repeats based on 'Number of Repeats'." // Removed color mention
        case "Strobe": // New
            return "<b>Strobe:</b> Three rapid flashes followed by a short pause. Repeats based on 'Number of Repeats'."
        case "Standby": // New
            return "<b>Standby:</b> Device stays on for 3 seconds, then off for 3 seconds. Repeats based on 'Number of Repeats'."
        case "Custom":
            return "<b>Custom:</b> Define your own flash duration and pause between flashes. Repeats based on 'Number of Repeats'."
        default:
            return "Select a pattern type."
    }
}

def patternPage() {
    dynamicPage(name: "patternPage", title: "", nextPage: "mainPage") { // Removed title
        display() // Added display call
        section(getFormat("header-blue", "${getImage("Blank")}" + "Pattern Type")) { // Renamed section
            input "patternType", "enum",
                title: "Pattern Type",
                options: ["Simple Flash", "Doorbell", "Emergency", "Strobe", "Standby", "Custom"], // Added Strobe & Standby
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
                paragraph "<b>Custom:</b> Define your own flash duration and pause between flashes. Repeats based on 'Number of Repeats'."
            } else {
                 paragraph "Select a pattern type." // Default message
            }
        }

        section(getFormat("header-blue", "${getImage("Blank")}" + "Pattern Repetition & Timing")) { // New section combining repeats and custom timing
            input "repeatCount", "number",
                title: "Number of Repeats (0 = indefinite)",
                required: true,
                defaultValue: 5, // Default to 5 repeats
                range: "0..100", // Limit to 100 repeats (adjust if needed)
                description: "Applies to all pattern types. 0 means the pattern repeats until stopped."

            if (patternType == "Custom") { // Only show duration inputs for Custom pattern
                 paragraph "<b>Custom Timing Settings</b>" // Added header for clarity
                 input "flashDuration", "number",
                    title: "Flash ON Duration (milliseconds)", // Changed title
                    required: true,
                    defaultValue: 1000, // Changed default
                    description: "Enter duration in milliseconds (e.g., 1500)." // Changed description

                 input "pauseDuration", "number",
                    title: "Flash OFF Duration (milliseconds)", // Changed title
                    required: true,
                    defaultValue: 1000, // Changed default
                    description: "Enter duration in milliseconds (e.g., 1500)." // Changed description
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
                title: "Restore Previous State After Alert",
                defaultValue: true
        }

        // Pattern Preview section removed
    }
}

def schedulePage() {
    dynamicPage(name: "schedulePage", title: "", nextPage: "mainPage") { // Removed title
        display() // Added display call
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
    dynamicPage(name: "advancedPage", title: "", nextPage: "mainPage") { // Removed title
        display() // Added display call
        section(getFormat("header-blue", "${getImage("Blank")}" + "Failsafe Settings")) {
            input "timeout", "number",
                title: "Pattern Timeout (minutes)",
                required: true,
                defaultValue: 5

            input "retryCount", "number", // Note: retryCount is defined but not used in pattern execution
                title: "Retry Attempts",
                required: true,
                defaultValue: 3

            input "cancelOnReverse", "bool", // Note: cancelOnReverse is defined but not used (switchHandler is inactive)
                title: "Cancel Alert When Trigger Reverses",
                defaultValue: true,
                description: "Example: Stop alert when a switch turns off"
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
    unschedule() // Ensure no old schedules persist
    initialize()
}

def initialize() {
    log.info "Initializing VisualAlert Child: ${alertName ?: 'Unnamed Alert'}"

    // Initialize state variables
    state.isAlertRunning = false // Use this flag for active alert state
    // isPreviewRunning state variable removed
    state.lastTriggerDevice = null
    atomicState.runLoop = false // Controls pattern execution loops
    state.previousStates = [:] // Store device states before alert
    // previewStates state variable removed

    // Store restorePrevious setting in state for reliable access (read via settings map)
    state.restorePreviousEnabled = (settings.restorePrevious as Boolean) ?: true // Default to true if null
    logDebug "initialize: Stored restorePreviousEnabled in state: ${state.restorePreviousEnabled}"

    // Initialize disabled state based on disableSwitch and disableCondition
    if (disableSwitch) {
        def conditionToDisable = settings.disableCondition ?: "ON" // Default to ON
        def currentSwitchValue = disableSwitch.currentValue("switch")
        state.isDisabled = currentSwitchValue?.equalsIgnoreCase(conditionToDisable) ?: false // Be safe if current value is null
        logDebug "initialize: Disable switch selected. Condition='${conditionToDisable}', CurrentValue='${currentSwitchValue}'. Initial isDisabled state: ${state.isDisabled}"
    } else {
        state.isDisabled = false // No disable switch selected
        logDebug "initialize: No disable switch selected. Initial isDisabled state: false"
    }

    // Subscribe to events
    subscribeToEvents()

    // Subscribe to app button events (Test/Stop Test buttons)
    subscribe(app, "buttonPressed", "appButtonHandler") // Re-enabled for Test button
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
        // Contact Sensors - Trigger
        if (contactSensors) {
            logDebug "Setting up contact sensor subscriptions for ${contactSensors.size()} devices"
            contactSensors.each { device ->
                logDebug "Subscribing to contact sensor: ${device.displayName} (ID: ${device.id})"
                subscribe(device, "contact.open", sensorHandler) // Subscribe to 'open' event
            }
        }

        // Motion Sensors - Trigger
        if (motionSensors) {
            logDebug "Setting up motion sensor subscriptions for ${motionSensors.size()} devices"
            motionSensors.each { device ->
                logDebug "Subscribing to motion sensor: ${device.displayName} (ID: ${device.id})"
                subscribe(device, "motion.active", sensorHandler) // Subscribe to 'active' event
            }
        }

// Smoke Sensors - Trigger
if (smokeSensors) {
    logDebug "Setting up smoke sensor subscriptions for ${smokeSensors.size()} devices"
    smokeSensors.each { device ->
        logDebug "Subscribing to smoke sensor: ${device.displayName} (ID: ${device.id})"
        subscribe(device, "smoke.detected", sensorHandler) // Subscribe to 'detected' event
    }
}

// CO Sensors - Trigger
if (coSensors) {
    logDebug "Setting up CO sensor subscriptions for ${coSensors.size()} devices"
    coSensors.each { device ->
        logDebug "Subscribing to CO sensor: ${device.displayName} (ID: ${device.id})"
        subscribe(device, "carbonMonoxide.detected", sensorHandler) // Subscribe to 'detected' event
    }
}
// Water Sensors - Trigger
if (waterSensors) {
    logDebug "Setting up water sensor subscriptions for ${waterSensors.size()} devices"
    waterSensors.each { device ->
        logDebug "Subscribing to water sensor: ${device.displayName} (ID: ${device.id})"
        subscribe(device, "water.wet", sensorHandler) // Subscribe to 'wet' event
    }
}

// Subscribe to the disable switch
if (disableSwitch) {
    logDebug "Subscribing to disable switch: ${disableSwitch.displayName}"
    subscribe(disableSwitch, "switch", disableSwitchHandler)
}

logDebug "Subscribed to all event sources"
logDebug "Subscribed to all event sources"
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
        if (!state.isDisabled) { // Only log and stop if changing state to disabled
            state.isDisabled = true
            logDebug "Alerts DISABLED because ${evt.displayName} turned ${eventValue} (matches condition '${conditionToDisable}')"
            // Stop any active alert immediately when disabled
            stopAlertImmediate(reason: "disabled by switch ${evt.displayName}")
        } else {
            logDebug "Alerts remain disabled (switch ${evt.displayName} is ${eventValue})"
        }
    } else {
        if (state.isDisabled) { // Only log if changing state to enabled
            state.isDisabled = false
            logDebug "Alerts ENABLED because ${evt.displayName} turned ${eventValue} (does not match condition '${conditionToDisable}')"
        } else {
            logDebug "Alerts remain enabled (switch ${evt.displayName} is ${eventValue})"
        }
        logDebug "Alerts enabled by switch: ${evt.displayName}"
    }
}

// --- Event Handlers ---

// Handles physical button presses for trigger and stop
def buttonHandler(evt) {
    // Check if disabled
    if (state.isDisabled) {
        logDebug "Button event ignored: Alert is disabled by switch."
        return
    }
    logDebug "*************** Button Handler ***************"
    logDebug "Button Event: device=${evt.device.displayName}, deviceId=${evt.deviceId}, value=${evt.value}"

    def deviceAndButton = "${evt.deviceId}:${evt.value}"
    logDebug "Button press detected - deviceAndButton: '${deviceAndButton}'"

    // Convert to string and trim for comparison
    def buttonToCheck = deviceAndButton.toString().trim()
    def selectedButtonsList = selectedButtons?.collect { it.toString().trim() }
    def selectedStopButtonsList = selectedStopButtons?.collect { it.toString().trim() }

    // First check if this is a stop button
    if (selectedStopButtonsList?.contains(buttonToCheck)) {
        log.info "Stop button match found! Button: ${buttonToCheck}"
        stopAlertImmediate(reason: "Stop button pressed: ${evt.device.displayName} Button ${evt.value}")
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
            logDebug "Stop Test button pressed - stopping alert"
            stopAlertImmediate(reason: "Stop Test button pressed")
            break

        default:
            logDebug "Unknown app button: $btn"
    }
}

// Handles switch 'on' and 'off' events for trigger switches
def switchHandler(evt) {
    // Check if disabled
    if (state.isDisabled) {
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
    } else {
        // Log if the event occurred but didn't match the configured trigger (e.g., switch turned ON but configured for OFF)
        logDebug "Switch event ignored: Event '${eventValue}' does not match configured trigger '${configuredTrigger}' for ${deviceName}"
    }
}
// Handles sensor 'detected' events for trigger sensors
def sensorHandler(evt) {
    // Check if disabled
    if (state.isDisabled) {
        logDebug "Sensor event ignored: Alert is disabled by switch."
        return
    }
    logDebug "*************** Sensor Handler ***************"
    logDebug "Sensor Event: device=${evt.device.displayName}, deviceId=${evt.deviceId}, name=${evt.name}, value=${evt.value}"

    def triggerReason = null
    // Check for smoke detection
    if (evt.name == "smoke" && evt.value == "detected") {
        triggerReason = "Smoke detected by ${evt.device.displayName}"
    }
    // Check for CO detection
    else if (evt.name == "carbonMonoxide" && evt.value == "detected") {
        triggerReason = "CO detected by ${evt.device.displayName}"
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
        startAlert(triggerReason)
    } else {
        // Log if the event wasn't a 'detected' state we care about
        logDebug "Sensor event ignored (name: ${evt.name}, value: ${evt.value})"
    }
}

// --- Core Alert Logic ---

// Checks if conditions (time, day, mode, presence, illuminance) are met
def isValidTrigger() {
    // Check time restrictions
    if (activeStart && activeEnd) {
        try {
            if (!timeOfDayIsBetween(toDateTime(activeStart), toDateTime(activeEnd), new Date(), location.timeZone)) {
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
    if (state.isAlertRunning) {
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
    state.isAlertRunning = true
    atomicState.runLoop = true // Allow pattern loops to run
    state.alertStart = now()
    state.alertReason = reason
    unschedule() // Clear any previous timeout or repeat schedules

    // Save current device states if restoration is enabled (using value stored in state)
    logDebug "startAlert: Checking restorePreviousEnabled state value: ${state.restorePreviousEnabled}"
    if (state.restorePreviousEnabled) {
        saveDeviceStates() // Saves to state.previousStates
    } else {
        logDebug "startAlert: Not saving device states as restorePreviousEnabled is false/null."
        state.previousStates = [:] // Clear previous states if not restoring
    }

    // --- Execute Pattern ---
    // This needs to happen *after* state is set and devices saved
    def patternInfo = getPatternInfo(emergency ? "Emergency" : null)
    // No need to pass restorePrevious in patternInfo anymore

    logDebug "Running pattern: ${patternInfo.type}"
    devices.each { device ->
        executeDevicePattern(device, patternInfo)
    }

    // --- Notifications & Timeout/Completion ---
    if (notifyStart) {
        sendNotification("VisualAlert: ${alertName} activated - ${reason}")
    }

    // Set timeout only for infinite patterns
    def effectiveTimeoutMinutes = timeout ?: 5 // Default to 5 minutes if not set
    if (patternInfo.isInfinite) {
        if (effectiveTimeoutMinutes > 0) {
            logDebug "Pattern is indefinite, setting timeout for ${effectiveTimeoutMinutes} minutes."
            def timeoutSeconds = effectiveTimeoutMinutes * 60
            runIn(timeoutSeconds, stopAlert, [data: [reason: "timeout"]])
        } else {
            logDebug "Pattern is indefinite and timeout is 0 or less, alert will run until stopped manually."
        }
    }
    // Finite patterns will now schedule their own stop/restore via executeDevicePattern
}

// Stops the alert due to timeout
def stopAlert(data) {
    def reason = data?.reason ?: "timeout"
    log.info "Stopping alert (stopAlert due to ${reason})"

    if (!state.isAlertRunning) {
        logDebug "Alert already stopped, ignoring stopAlert call."
        return
    }

    // --- Stop Pattern & Restore State ---
    unschedule() // Clear any remaining schedules
    atomicState.runLoop = false // Signal pattern loops to stop
    state.isAlertRunning = false // Mark alert as stopped

    // Restore previous states if needed (using value stored in state)
    logDebug "stopAlert: Checking restorePreviousEnabled state value: ${state.restorePreviousEnabled}"
    if (state.restorePreviousEnabled) {
        restoreDeviceStates(state.previousStates) // Explicitly pass alert states
    } else {
        // If not restoring, ensure devices are turned off
        logDebug "Not restoring previous state, turning devices off."
        devices?.off()
    }

    // --- Notifications ---
    if (notifyEnd) {
        sendNotification("VisualAlert: ${alertName} deactivated - ${reason}")
    }

    // Clean up state
    state.lastTriggerDevice = null
    state.alertStart = null
    state.alertReason = null
    // state.previousStates is cleared by restoreDeviceStates
}

// Stops the alert immediately. Can be called directly (params map) or via runIn (data map)
def stopAlertImmediate(evtOrParams = null) {
    // Determine if called by runIn (evtOrParams will have a 'data' key) or directly
    def params = evtOrParams instanceof Map ? evtOrParams : [:]
    def data = evtOrParams?.data instanceof Map ? evtOrParams.data : [:]

    def reason = data.reason ?: params.reason ?: "manual stop"
    // Default sendNotify differently depending on source? Let's default to true unless specified false.
    boolean sendNotify = data.get("sendNotify", params.get("sendNotify", true))

    log.info "Stopping alert immediately (stopAlertImmediate): ${reason}"

    // Signal pattern loops to stop *immediately*
    atomicState.runLoop = false
    logDebug "stopAlertImmediate: Set atomicState.runLoop to false"

    // Determine if we need to restore based on state variable
    boolean shouldRestore = state.restorePreviousEnabled ?: false
    logDebug "stopAlertImmediate: Checking restorePreviousEnabled state value: ${shouldRestore}"

    // Check if alert is running
    boolean wasAlertRunning = state.isAlertRunning
    // wasPreviewRunning variable removed

    if (!wasAlertRunning) {
        logDebug "Alert not running when stopAlertImmediate called, ignoring stop actions."
        return
    }

    // --- Restore State (if needed) ---
    // This now happens BEFORE marking the state as stopped
    if (shouldRestore) {
        logDebug "stopAlertImmediate: Restore is enabled, proceeding with restore."
        // Restore from the alert's previous state map
        if (wasAlertRunning && state.previousStates && !state.previousStates.isEmpty()) {
            logDebug("Restoring from alert's previousStates")
            restoreDeviceStates(state.previousStates) // Restore from alert state
        } else {
            logDebug("No previous states found for running alert, turning devices off.")
            devices?.off()
        }
    } else {
        // If not restoring, ensure devices are turned off
        logDebug "Not restoring previous state (restorePreviousEnabled is false/null), turning devices off."
        devices?.off()
    }

    // --- Final State Update & Cleanup ---
    unschedule() // Clear any remaining schedules
    state.isAlertRunning = false // Mark alert as stopped *after* restore attempt
    // isPreviewRunning state update removed
    logDebug "State flag isAlertRunning set to false."

    // --- Notifications ---
    // Only send notification if an alert was running (not just preview) and requested
    if (wasAlertRunning && notifyEnd && sendNotify) {
        sendNotification("VisualAlert: ${alertName} deactivated - ${reason}")
    }

    // Clean up other state variables
    state.lastTriggerDevice = null
    state.alertStart = null
    state.alertReason = null
    // state.previousStates is cleared by restoreDeviceStates
}


// --- State Management ---

// Saves current device states before starting an alert
def saveDeviceStates() {
    log.info "Saving device states before alert"
    state.previousStates = [:] // Clear any old states first

    devices.each { device ->
        try {
            def currentSwitch = device.currentValue("switch")
            def currentLevel = hasLevelCapability(device) ? device.currentValue("level") : null
            def currentHue = hasColorCapability(device) ? device.currentValue("hue") : null
            def currentSat = hasColorCapability(device) ? device.currentValue("saturation") : null
            def currentCT = hasColorTemperature(device) ? device.currentValue("colorTemperature") : null

            // Ensure we have a valid switch state
            if (currentSwitch != "on" && currentSwitch != "off") {
                logWarn "Device ${device.displayName} reported invalid switch state '${currentSwitch}', assuming 'off'."
                currentSwitch = "off"
            }

            def stateObj = [
                switch: currentSwitch,
                level: currentLevel,
                hue: currentHue,
                saturation: currentSat,
                colorTemperature: currentCT
            ]

            state.previousStates[device.id.toString()] = stateObj
            logDebug "Saved initial state for ${device.displayName}: ${stateObj}"
        } catch (e) {
            log.error "Error saving state for ${device.displayName}: ${e.message}"
        }
    }
    logDebug "Finished saving device states. Count: ${state.previousStates?.size() ?: 0}. States: ${state.previousStates}"
}

// Restores device states after an alert stops
def restoreDeviceStates(Map statesToRestore) { // Now requires the map to restore from
    if (!statesToRestore || statesToRestore.isEmpty()) {
        log.warn "restoreDeviceStates called but no states found to restore. Turning devices off."
        devices?.off()
        // Clear the specific map that was passed (or intended to be passed)
        if (statesToRestore == state.previousStates) state.previousStates = [:]
        // Preview-related map clearing removed
        return
    }

    log.info "Restoring device states. Count: ${statesToRestore?.size() ?: 0}. States: ${statesToRestore}"

    // First, turn off all devices to ensure a clean state before restoring ON states
    devices?.each { device ->
        try {
            device.off()
        } catch (Exception e) {
            log.warn "Error turning off ${device.displayName} during pre-restore: ${e.message}"
        }
    }
    pauseExecution(1000) // Give devices time to settle

    // Now restore previous states
    devices.each { device ->
        def deviceIdStr = device.id.toString()
        def prevState = statesToRestore[deviceIdStr]

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

                    // Verification step (optional but recommended for problematic devices)
                    pauseExecution(1000)
                    if (device.currentValue("switch") != "on") {
                        log.warn "Device ${device.displayName} did not report ON after restore (current: ${device.currentValue('switch')}), trying ON command again."
                        device.on()
                    }
                } else {
                    // Ensure device is OFF (already turned off above, but double-check)
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

    // Clear the specific map used for restoration after attempting restore
    if (statesToRestore == state.previousStates) state.previousStates = [:]
    // Preview-related map clearing removed
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
        } else if (attrs.hue != null && attrs.saturation != null && hasColorCapability(device)) {
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
                // Removed extra flashes, repeatCount handles repetition
            ]
            // info.repeatCount is set above from input
            break
        case "Emergency":
            info.commands = [
                [on: true, duration: 300], [on: false, duration: 300]
            ]
            // info.repeatCount is set above from input
            // info.color = "#FF0000" // Removed hardcoded red color
            info.level = 100 // Force full brightness
            break
        case "Strobe": // New Strobe pattern
            info.commands = [
                [on: true, duration: 200], [on: false, duration: 200],
                [on: true, duration: 200], [on: false, duration: 200],
                [on: true, duration: 200], [on: false, duration: 1500] // Longer pause
            ]
            // info.repeatCount is set above from input
            break
        case "Standby": // New Standby pattern
             info.commands = [
                [on: true, duration: 3000], // On for 3 seconds
                [on: false, duration: 3000] // Off for 3 seconds
            ]
            // info.repeatCount is set above from input
            break
        case "Custom":
            // Removed flashCount dependency
            def onDurationSetting = settings.flashDuration
            def offDurationSetting = settings.pauseDuration
            def onDurationMs = onDurationSetting ?: 1000 // Use setting directly, default to 1000ms if null
            def offDurationMs = offDurationSetting ?: 1000 // Use setting directly, default to 1000ms if null
            info.commands = [
                [on: true, duration: onDurationMs], [on: false, duration: offDurationMs]
            ]
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
    // Duplicate offLevel line removed

    // logDebug "Generated pattern info: ${info}" // Log moved up
    return info
}

// Executes a pattern sequence on a single device
def executeDevicePattern(device, Map patternInfo) {
    logDebug "Executing pattern ${patternInfo.type} on ${device.displayName}"
    def commands = patternInfo.commands
    def repeatCount = patternInfo.repeatCount // Number of loops (1 if infinite)
    def isInfinite = patternInfo.isInfinite
    def loopCounter = 0
    boolean completedNormally = true // Assume completion unless stopped

    // --- Prepare device ---
    // Set initial color/level if specified, BEFORE the loop starts
    try {
        def initialAttrs = [:]
        if (patternInfo.color && hasColorCapability(device)) initialAttrs.color = patternInfo.color
        if (patternInfo.level != null && hasLevelCapability(device)) initialAttrs.level = patternInfo.level

        if (initialAttrs) {
            setDeviceAttributes(device, initialAttrs)
            pauseExecution(500) // Allow attributes to set
        }
    } catch (e) {
        log.warn "Error setting initial attributes for ${device.displayName}: ${e.message}"
    }

    // --- Execution Loop ---
    while(atomicState.runLoop && (isInfinite || loopCounter < repeatCount)) {
        logDebug "Pattern loop iteration ${loopCounter + 1} for ${device.displayName}. runLoop: ${atomicState.runLoop}"

        for (cmd in commands) {
            // Check stop flag before each command
            if (!atomicState.runLoop) {
                logDebug "Stop requested during pattern execution for ${device.displayName}"
                completedNormally = false
                break // Exit inner command loop
            }

            try {
                boolean turnOn = cmd.on
                long duration = cmd.duration

                if (turnOn) {
                    // Set color and level for the ON state
                    def targetLevel = patternInfo.level ?: 100 // Default to 100 if not set

                    // Apply color FIRST if specified (using the HSL map)
                    if (patternInfo.colorMap && hasColorCapability(device)) {
                        try {
                            logDebug "Setting color map for ${device.displayName}: ${patternInfo.colorMap}"
                            device.setColor(patternInfo.colorMap) // Pass HSL map
                            pauseExecution(300) // Pause after color set
                        } catch (e) {
                            log.warn "Error setting color map for ${device.displayName} during ON phase: ${e.message}"
                        }
                    }

                    // Apply level SECOND
                    if (hasLevelCapability(device)) {
                         try {
                            logDebug "Setting level for ${device.displayName}: ${targetLevel}"
                            device.setLevel(targetLevel)
                            pauseExecution(200) // Pause after level set
                         } catch (e) {
                            log.warn "Error setting level for ${device.displayName} during ON phase: ${e.message}"
                         }
                    }

                    // Ensure device is ON THIRD (after color/level)
                    if (device.currentValue("switch") != "on") {
                        logDebug "Turning ON ${device.displayName}"
                        device.on()
                        pauseExecution(100) // Small pause after turning on
                    }
                    // Log actual level after attempting to set everything
                    logDebug "${device.displayName} is ON at level ${device.currentValue('level')}" + (patternInfo.colorMap ? " with color map ${patternInfo.colorMap}" : "")

                } else { // OFF state logic
                    def targetOffLevel = patternInfo.offLevel ?: 0 // Default to 0 if not set or 0
                    if (targetOffLevel > 0 && hasLevelCapability(device)) {
                        // Set the device to the specified low level
                        logDebug "Setting OFF level for ${device.displayName}: ${targetOffLevel}"
                        device.setLevel(targetOffLevel)
                        // Ensure the device is ON to maintain the dim level
                        if (device.currentValue("switch") != "on") {
                             logDebug "Turning ON ${device.displayName} to maintain OFF level"
                             device.on() // Turn ON only if it's currently OFF
                             pauseExecution(100)
                        }
                        logDebug "${device.displayName} set to OFF Level ${targetOffLevel}"
                    } else {
                        // Standard OFF behavior
                        logDebug "Turning OFF ${device.displayName}"
                        device.off()
                        logDebug "${device.displayName} is OFF"
                    }
                }
                pauseExecution(duration) // Apply the pause AFTER setting the state

            } catch (Exception e) {
                log.error "Error executing pattern step on ${device.displayName}: ${e.message}"
                completedNormally = false
                // Consider stopping the entire alert on error?
                // stopAlertImmediate(reason: "Error executing pattern on ${device.displayName}")
                break // Exit inner command loop
            }
        } // End command loop

        if (!atomicState.runLoop || !completedNormally) {
            break // Exit outer while loop if stopped or error
        }
        loopCounter++
    } // End while loop

    logDebug "Pattern loop finished for ${device.displayName}. Completed Normally: ${completedNormally}, Infinite: ${isInfinite}"

    // --- Final State & Cleanup ---
    // If the pattern completed normally AND it's finite, schedule stopAlertImmediate to handle cleanup/restore
    if (completedNormally && !isInfinite) {
        logDebug "Finite pattern completed normally, scheduling stopAlertImmediate via runIn to restore/cleanup."
        // Use string method name and pass params via data map
        runIn(1, "stopAlertImmediate", [data: [reason: "pattern completed", sendNotify: true], overwrite: true])
    }
    // If pattern was stopped early (!completedNormally), the stop function was already called.
    // If pattern is infinite, it relies on timeout or manual stop.
}

// Unused restoreDeviceState helper method removed


// Helper to set device attributes (level, color, CT)
private void setDeviceAttributes(device, Map attrs) {
    logDebug "Setting attributes for ${device.displayName}: ${attrs}"
    try {
        // Set Color Temp or Color first
        if (attrs.colorTemperature != null && hasColorTemperature(device)) {
            device.setColorTemperature(attrs.colorTemperature)
            logDebug "Set Color Temperature: ${attrs.colorTemperature}"
            pauseExecution(300)
        } else if (attrs.colorMap && hasColorCapability(device)) { // Check for colorMap first
             try {
                // Use setColor with the HSL map
                device.setColor(attrs.colorMap)
                logDebug "Set Color using setColor with Map: ${attrs.colorMap}"
                pauseExecution(300)
            } catch (e) {
                 logWarn "setColor with Map failed for ${device.displayName}: ${e.message}"
                 // Optional: Fallback to trying hex if map fails? Might be risky.
            }
        } else if (attrs.color && hasColorCapability(device)) { // Fallback to original color attribute if colorMap not present
             try {
                // Use setColor map if possible (original logic)
                device.setColor(attrs.color) // Assumes color is a hex string or map
                logDebug "Set Color using setColor (fallback): ${attrs.color}"
                pauseExecution(300)
            } catch (e) {
                 logWarn "setColor (fallback) failed for ${device.displayName}: ${e.message}"
            }
        }

        // Set Level
        if (attrs.level != null && hasLevelCapability(device)) {
            device.setLevel(attrs.level)
            logDebug "Set Level: ${attrs.level}"
            pauseExecution(300)
        }
    } catch (Exception e) {
        log.warn "Error setting attributes for ${device.displayName}: ${e.message}"
    }
}

// --- Preview Logic Removed ---
// Preview functions (previewPattern, stopPreviewImmediate) removed.


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
        // Use the level passed into the function, which comes from alertLevel setting
        Integer hubLevel = level ?: 100

        // Clamp values just in case
        hubHue = Math.max(0, Math.min(100, hubHue))
        hubSaturation = Math.max(0, Math.min(100, hubSaturation))
        hubLevel = Math.max(1, Math.min(100, hubLevel)) // Level should be 1-100

        Map hslMap = [hue: hubHue, saturation: hubSaturation, level: hubLevel]
        // logDebug "Converted Hex ${hexColor} to HSL Map: ${hslMap}" // Keep commented out for potential future debugging
        return hslMap

    } catch (Exception e) {
        log.error "Error converting hex ${hexColor} to HSL: [${e.class.name}] ${e.message}"
        // Default to White on error
        return [hue: 0, saturation: 0, level: level ?: 100]
    }
}


// Check if device has ColorControl capability or related commands/attributes
private boolean hasColorCapability(device) {
    try {
        return device.hasCapability("ColorControl") ||
               device.hasCommand("setColor") ||
               device.hasCommand("setHue") ||
               device.hasAttribute("hue")
    } catch (e) { return false }
}

// Check if device has SwitchLevel capability or related commands/attributes
private boolean hasLevelCapability(device) {
     try {
        return device.hasCapability("SwitchLevel") ||
               device.hasCommand("setLevel") ||
               device.hasAttribute("level")
    } catch (e) { return false }
}

// Check if device has ColorTemperature capability or related commands/attributes
private boolean hasColorTemperature(device) {
     try {
        return device.hasCapability("ColorTemperature") ||
               device.hasCommand("setColorTemperature") ||
               device.hasAttribute("colorTemperature")
    } catch (e) { return false }
}

// Log debug messages if enabled
private void logDebug(String msg) {
    if (enableLogging) {
        log.debug msg
    }
}

// Send notifications
private void sendNotification(String message) {
    logDebug "Sending notification: ${message}"
    // Send to notification devices if configured
    if (notificationDevices) {
        notificationDevices.each { device ->
            try {
                device.deviceNotification(message)
            } catch (e) {
                log.error "Error sending notification to device ${device.displayName}: ${e.message}"
            }
        }
    }

    // Send to parent app for system-wide notifications (with checks)
    if (parent) {
        try {
            // Check if parent has the setting and it's enabled (using respondsTo for safety)
            boolean parentNotifyEnabled = false
            if (parent.respondsTo("getEnableNotifications")) {
                parentNotifyEnabled = parent.getEnableNotifications()
            } else if (parent.respondsTo("getSettings") && parent.settings?.enableNotifications) {
                // Alternative check via settings map if getter doesn't exist
                parentNotifyEnabled = parent.settings.enableNotifications
            }

            if (parentNotifyEnabled) {
                 // Check if parent actually has the method before calling
                 if (parent.respondsTo("sendNotification", String)) {
                    parent.sendNotification(message)
                    logDebug "Sent notification to parent app"
                 } else {
                    log.warn "Parent app does not have a sendNotification(String) method."
                 }
            } else {
                logDebug "Parent app notifications are disabled."
            }
        } catch (Exception e) {
            log.error "Error sending notification to parent app: ${e.message}"
            // Continue execution even if parent notification fails
        }
    }
}

// Helper method to get a device by ID from the configured list
def getDeviceById(deviceId) {
    return devices.find { it.id == deviceId }
}

// Keep isActive for potential external checks, but map it to the new state
def isActive() {
    return state.isAlertRunning ?: false
}

// Helper function to determine contrast color (black or white) for text on a colored background
private String getContrastColor(String hexColor) {
    if (!hexColor || hexColor.length() < 7 || !hexColor.startsWith("#")) {
        return "#000000" // Default to black if color is invalid
    }
    try {
        // Simplified contrast logic (adjust as needed)
        def color = hexColor.replace("#", "")
        def r = Integer.parseInt(color.substring(0, 2), 16)
        def g = Integer.parseInt(color.substring(2, 4), 16)
        def b = Integer.parseInt(color.substring(4, 6), 16)
        // Using standard luminance formula
        def luminance = (0.2126 * r + 0.7152 * g + 0.0722 * b) / 255
        // logDebug "Calculated luminance for ${hexColor}: ${luminance}" // Optional debug
        return luminance > 0.5 ? "#000000" : "#FFFFFF" // Return black for light backgrounds, white for dark
    } catch (Exception e) {
        log.warn "Error calculating contrast color for ${hexColor}: ${e.message}"
        return "#000000" // Default to black on error
    }
} // Added missing closing brace for getContrastColor

    // Display custom styled header
    def display() {
        // Use settings.alertName, provide default if null/empty
        def childName = settings.alertName ?: "New Alert"
        def headerText = "VisualAlert - ${childName}"
        section() { // Use an empty section to contain the paragraph
            paragraph "<h2 style='color:#007bff; font-weight:bold; text-align:center; font-size:1.5em;'>${headerText}</h2>" // Increased font size
            // Optionally add a subtitle or line like in The Flasher
            // paragraph "<div style='color:#007bff; text-align:center;'>Child Alert Configuration</div>"
            paragraph "<hr style='background-color:#007bff; height: 1px; border: 0;' />" // Add a separator line
        }
    }
// Removed misplaced closing brace

// ***** Style Formatting Methods (Adapted from @Stephack Code / @bptworld The Flasher) *****
def getFormat(type, myText="") {
    // Using a standard blue color
    if(type == "header-blue") return "<div style='color:#ffffff;font-weight: bold;background-color:#007bff;border: 1px solid;box-shadow: 2px 3px #A9A9A9;padding: 8px;border-radius: 8px;'>${myText}</div>" // Increased padding and added border-radius
    // Add other formats here if needed in the future
    return myText // Default return
}

def getImage(imgName) {
    // Using bptworld's image repo for the blank image placeholder - requires internet access from the hub
    def iconUrl = "https://raw.githubusercontent.com/bptworld/Hubitat/master/resources/images/"
    // Using a 1x1 transparent pixel image for spacing
    if(imgName == "Blank") return "<img src='${iconUrl}blank.png' width='1' height='1' style='margin-right: 5px;'>"
    // Add other images here if needed
    return "" // Default return
}