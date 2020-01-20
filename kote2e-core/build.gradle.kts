dependencies {
    implementation(project(":kote2e-matcher"))

    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("test"))

    implementation("com.jayway.jsonpath:json-path:2.4.0")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.10.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.10.2")

    implementation("com.squareup.okhttp3:okhttp:4.3.1")
    implementation("com.squareup.okhttp3:logging-interceptor:4.3.1")

    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("org.slf4j:jul-to-slf4j:1.7.30")
    implementation("org.slf4j:slf4j-api:1.7.30")

    testImplementation("org.junit.jupiter:junit-jupiter:5.5.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.5.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.5.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.5.2")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.3.1")
}

