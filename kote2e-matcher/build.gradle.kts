dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("test"))

    implementation("com.fasterxml.jackson.core:jackson-databind:2.10.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.10.2")

    testImplementation("org.junit.jupiter:junit-jupiter:5.5.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.5.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.5.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.5.2")
}

