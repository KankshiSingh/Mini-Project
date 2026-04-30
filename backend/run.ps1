$ErrorActionPreference = "Stop"
$scriptPath = Split-Path -Parent $MyInvocation.MyCommand.Path
$backendPath = $scriptPath

# Using the system's installed Java 24
$env:JAVA_HOME = "C:\Program Files\Java\jdk-24"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

Set-Location $backendPath
& "$backendPath\maven\apache-maven-3.9.6\bin\mvn.cmd" spring-boot:run