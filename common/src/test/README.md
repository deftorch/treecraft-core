# Testing Documentation

This directory contains tests for the `common` module.

## Setup

The test infrastructure uses JUnit 5 and Mockito.

## Mock Utilities

The `com.treecraft.core.test.util` package contains helper classes for mocking Minecraft objects.

### MockBlockState

Used to create mocked `BlockState` objects.

```java
MockBlockState mock = new MockBlockState();
BlockState state = mock.get();

// Mock behavior
mock.withSound(SoundType.WOOD);
```

### MockLevel

Used to create mocked `Level` objects.

```java
MockLevel mockLevel = new MockLevel();
Level level = mockLevel.get();

// Mock behavior
mockLevel.withBlock(pos, blockState);
```

### MockBlockPos

Used to create `BlockPos` objects.

```java
BlockPos pos = MockBlockPos.at(0, 0, 0);
```

## Running Tests

Run tests using Gradle:

```bash
./gradlew :common:test
```
