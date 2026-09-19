plugins {
    id("java")
    id("org.springframework.boot") version "3.3.0"
    id("io.spring.dependency-management") version "1.1.5"
}

group = "dev.ferrox"
version = "1.0.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    // Rely on composite build substitutions for ferrox-java modules
    implementation("dev.ferrox:ferrox-java-core")
    implementation("dev.ferrox:ferrox-java-security")
    implementation("dev.ferrox:ferrox-java-cqrs")
    implementation("dev.ferrox:ferrox-java-data")
    implementation("dev.ferrox:ferrox-java-web")
    implementation("dev.ferrox:ferrox-java-event-manager")
    implementation("dev.ferrox:ferrox-java-offheap")
    
    // APT processor for @FerroxEntity
    compileOnly("dev.ferrox:ferrox-java-crud-gen")
    annotationProcessor("dev.ferrox:ferrox-java-crud-gen")
    
    implementation("org.springframework.boot:spring-boot-starter-web")
    
    // In a real project, we'd add spring-boot-starter-data-jpa
}
