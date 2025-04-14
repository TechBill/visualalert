/**
 *  VisualAlert
 *
 *  Advanced visual notification system optimized for deaf and hard of hearing users.
 *  Provides reliable, customizable visual alerts for Hubitat home automation.
 *
 *  Copyright 2024
 *  Licensed under the Apache License, Version 2.0
 *
 *  Version: 1.2.0
 */

definition(
    name: "VisualAlert",
    namespace: "TechBill",
    author: "Bill Fleming",
    description: "Advanced visual notification system using smart devices for alerts and notifications",
    category: "Convenience",
    iconUrl: "http://cdn.device-icons.smartthings.com/Lighting/light11-icn.png",
    iconX2Url: "http://cdn.device-icons.smartthings.com/Lighting/light11-icn@2x.png",
    iconX3Url: "http://cdn.device-icons.smartthings.com/Lighting/light11-icn@3x.png",
    singleInstance: true
)

preferences {
    page(name: "mainPage")
}

def mainPage() {
    // Check if this is initial setup or the app is already installed
    def isInstalled = app.getInstallationState() == "COMPLETE"

    dynamicPage(name: "mainPage", title: "", install: true, uninstall: true, nextPage: null) { // Removed title
        display() // Added display call
        section(getFormat("header-blue", "${getImage("Blank")}" + "System Status")) {
            paragraph "VisualAlert System v${version()}"
            if (isInstalled) {
                paragraph "Active Alerts: ${childApps.size()}"
            }
        }

        if (isInstalled) {
            // Only show child app creation if the parent app is already installed
            section(getFormat("header-blue", "${getImage("Blank")}" + "VisualAlert Child(s)")) { // Changed section title
                // Removed description paragraph
                app(name: "visualAlertChildApps",
                    appName: "VisualAlert Child",
                    namespace: "TechBill",
                    title: "Create VisualAlert Child", // Changed button text
                    multiple: true)
            }
        } else {
            section("Initial Setup") {
                paragraph "<b>Click Done to add VisualAlert to your Apps.</b>"
                paragraph "After installation, you can open VisualAlert from your Apps list to create alerts."
            }
        }

        section(getFormat("header-blue", "${getImage("Blank")}" + "Global Settings")) {
            input "enableLogging", "bool",
                title: "Enable Debug Logging",
                defaultValue: false
            input "enableNotifications", "bool",
                title: "Enable System Notifications",
                defaultValue: true
        }

        section(getFormat("header-blue", "${getImage("Blank")}" + "System Health")) {
            paragraph "Last System Check: ${state.lastHealthCheck ?: 'Not Run'}"
            input "btnHealthCheck", "button",
                title: "Run System Check"
        }

        section("Support") {
            paragraph "Donations are always appreciated!"
            paragraph "<a href='https://paypal.me/techbill?country.x=US&locale.x=en_US' target='_blank'>💸 Donate via PayPal</a>"
            paragraph "<a href='https://www.buymeacoffee.com/techbill' target='_blank'>☕ Buy Me a Coffee</a>"
        }
    }
}

def installed() {
    logDebug "VisualAlert installed"
    initialize()
}

def updated() {
    logDebug "VisualAlert updated"
    unsubscribe()
    initialize()
}

def initialize() {
    log.info "Initializing VisualAlert System"
    runSystemHealthCheck()
    state.version = version()
}

def version() {
    return "1.0.32"
}

def runSystemHealthCheck() {
    log.info "Running system health check"
    def status = [:]

    // Check child apps
    status.childApps = childApps.size()
    status.activeAlerts = childApps.count { it.isActive() ?: false }

    // Check system resources - don't use runtime.freeMemory() as it causes errors
    status.memory = "Not Available in Hubitat"

    state.lastHealthCheck = new Date().format("yyyy-MM-dd HH:mm:ss")
    state.systemStatus = status

    return status
}

def appButtonHandler(btn) {
    switch(btn) {
        case "btnHealthCheck":
            runSystemHealthCheck()
            break
        default:
            logDebug "Unknown button: $btn"
    }
}

private void logDebug(String msg) {
    if (enableLogging) {
        log.debug msg
    }
}

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

// Display custom styled header for Parent App
def display() {
    def headerText = "VisualAlert" // Static title for parent
    section() { // Use an empty section to contain the paragraph
        paragraph "<h2 style='color:#007bff; font-weight:bold; text-align:center; font-size:1.5em;'>${headerText}</h2>" // Increased font size
        paragraph "<hr style='background-color:#007bff; height: 1px; border: 0;' />" // Add a separator line
    }
}