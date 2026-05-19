$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
Set-Location "c:\Users\zorao\OneDrive\Documents\PNXplugins\InvSee-PNX"
& ".\gradlew.bat" build
