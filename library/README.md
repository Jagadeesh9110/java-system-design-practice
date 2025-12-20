Now can you please the below one with markdown format 
# Library Management System – Low Level Design

**A production-grade, interview-oriented Low Level Design implementation demonstrating enterprise Java development practices, SOLID principles, and systematic object-oriented architecture.**

---

## Table of Contents

- [Overview](#overview)
- [System Architecture](#system-architecture)
- [Problem Statement](#problem-statement)
- [Requirements Analysis](#requirements-analysis)
- [Domain Model](#domain-model)
- [Design Decisions](#design-decisions)
- [Implementation Details](#implementation-details)
- [Error Handling Strategy](#error-handling-strategy)
- [Runtime Behavior](#runtime-behavior)
- [Testing Strategy](#testing-strategy)
- [Future Enhancements](#future-enhancements)
- [Developer Notes](#developer-notes)

---

## Overview

### Project Metadata

```
Project Name:    Library Management System
Architecture:    Domain-Driven Design (DDD)
Language:        Java 17+
Design Level:    Low Level Design (LLD)
Storage:         In-Memory
Concurrency:     Single-threaded
```

### Key Characteristics

**This is NOT:**
- A simple CRUD application
- A database-backed system
- A UI/API-driven application
- A concurrent/distributed system

**This IS:**
- A domain-driven, object-oriented system
- An exercise in clean code architecture
- A demonstration of SOLID principles
- An interview-ready LLD implementation
- A foundation for enterprise-grade systems

### Design Philosophy

The system prioritizes:
1. **Correctness** over convenience
2. **Explicitness** over implicitness
3. **Maintainability** over brevity
4. **Domain modeling** over technical patterns
5. **Fail-fast behavior** over silent failures

---

## System Architecture

### Architectural Style

```
Layered Architecture with Domain-Driven Design

┌─────────────────────────────────────┐
│         Presentation Layer          │ (Console Interface)
│        (Future: REST API)           │
└─────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────┐
│       Application Layer             │ (Library - Orchestrator)
│     (Workflow Coordination)         │
└─────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────┐
│         Domain Layer                │
│  ┌─────────────────────────────┐   │
│  │   Entities & Value Objects  │   │ (Book, BookCopy, User)
│  ├─────────────────────────────┤   │
│  │   Aggregates                │   │ (BookInventory)
│  ├─────────────────────────────┤   │
│  │   Domain Services           │   │ (Business Logic)
│  └─────────────────────────────┘   │
└─────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────┐
│      Infrastructure Layer           │ (In-Memory Collections)
│    (Future: Database/Cache)         │
└─────────────────────────────────────┘
```

### Core Design Patterns Applied

| Pattern | Applied To | Justification |
|---------|-----------|---------------|
| **Aggregate Root** | BookInventory | Maintains consistency boundaries for books and copies |
| **Value Object** | Book | Immutable, identity-based on bookId |
| **Entity** | BookCopy, User | Mutable state, unique identity |
| **Orchestrator** | Library | Coordinates workflows, ensures atomicity |
| **Fail-Fast** | All operations | Domain rule violations throw immediately |

---

## Problem Statement

### Business Context

A library needs a software system to:
- Manage a catalog of books with multiple physical copies
- Track registered members and their borrowing activity
- Enforce business rules (borrowing limits, availability)
- Maintain data consistency across all operations
- Support future extensions (fines, reservations, policies)

### Technical Challenge

Design a system that:
- **Models reality accurately**: Book ≠ BookCopy
- **Prevents invalid states**: No half-completed transactions
- **Enforces rules by design**: Not by conditional checks
- **Separates concerns**: Clear ownership of responsibilities
- **Scales conceptually**: Easy to add features without refactoring

### Success Criteria

A successful implementation must demonstrate:
1. Correct entity modeling (domain-driven)
2. Clear separation of responsibilities (SRP)
3. Atomic transaction handling (consistency)
4. Extensible architecture (OCP)
5. Proper exception hierarchy (fail-fast)
6. Justifiable data structure choices (performance)

---

## Requirements Analysis

### Functional Requirements

#### FR1: Book Management
- **FR1.1**: Add book with immutable metadata (title, author, ISBN)
- **FR1.2**: Add multiple physical copies of a book
- **FR1.3**: Remove book only if all copies are returned
- **FR1.4**: Track total, available, and issued copies
- **FR1.5**: Validate book existence before operations

#### FR2: User Management
- **FR2.1**: Register user with unique identifier
- **FR2.2**: Track borrowed copies per user
- **FR2.3**: Enforce borrowing limit (configurable, default: 5)
- **FR2.4**: Prevent duplicate user registration

#### FR3: Core Operations
- **FR3.1**: Borrow Book (Atomic)
  - Validate user eligibility
  - Check book availability
  - Allocate specific copy
  - Update user state
  - Update inventory state
  - Rollback on any failure

- **FR3.2**: Return Book (Atomic)
  - Validate user-copy ownership
  - Update user state
  - Release copy to inventory
  - Prevent invalid returns

### Non-Functional Requirements

#### Performance
| Operation | Complexity | Data Structure |
|-----------|-----------|----------------|
| User lookup | O(1) | HashMap |
| Book lookup | O(1) | HashMap |
| Copy allocation | O(1) | Deque.removeFirst() |
| Copy release | O(1) | Deque.addLast() |
| Ownership check | O(1) | HashSet.contains() |

#### Reliability
- **Atomicity**: All operations are all-or-nothing
- **Consistency**: No partial state updates permitted
- **Isolation**: Single-threaded (future: pessimistic locking)
- **Durability**: In-memory only (future: persistence layer)

#### Maintainability
- **Single Responsibility**: Each class has one clear purpose
- **Open-Closed**: Extension without modification
- **Dependency Inversion**: Abstract contracts, not implementations
- **Interface Segregation**: Minimal, focused interfaces

#### Extensibility
Designed for future additions:
- Borrowing policies (Strategy pattern)
- Fine calculation (Policy abstraction)
- Reservation system (Queue-based)
- Multiple libraries (Aggregate pattern)
- Persistence (Repository pattern)

### Assumptions & Constraints

**Business Assumptions:**
1. Single library instance manages all operations
2. Book metadata never changes after creation
3. Physical copies are fungible (any copy of same book is equivalent)
4. Users have uniform borrowing limits initially
5. No concept of "due dates" in current phase

**Technical Constraints:**
1. In-memory storage (no database)
2. Single-threaded execution
3. No authentication/authorization
4. No external integrations
5. Console-based interaction only

**Design Constraints:**
1. Book ≠ BookCopy (mandatory separation)
2. Issue/return at copy level (not book level)
3. Fail-fast exception handling
4. No silent failures or logging workarounds
5. RuntimeException for domain violations

---

## Domain Model

### Entity Relationship Diagram

```
┌─────────────────────────┐
│        Library          │
│   (Orchestrator)        │
│ ─────────────────────── │
│ - users: Map<String,    │
│           User>         │
│ - inventory:            │
│   BookInventory         │
└───────────┬─────────────┘
            │ manages
            │
    ┌───────┴────────┐
    │                │
    ▼                ▼
┌────────────┐  ┌──────────────────┐
│    User    │  │  BookInventory   │
│            │  │   (Aggregate)    │
│ ────────── │  │ ──────────────── │
│ - userId   │  │ - books: Map     │
│ - name     │  │ - allCopies: Map │
│ - borrowed │  │ - available: Map │
│   Copies   │  └────────┬─────────┘
└────────────┘           │ contains
                         │
                    ┌────┴──────┐
                    │           │
                    ▼           ▼
              ┌──────────┐  ┌───────────┐
              │   Book   │  │ BookCopy  │
              │  (VO)    │  │ (Entity)  │
              │ ──────── │  │ ───────── │
              │ - bookId │  │ - copyId  │
              │ - title  │  │ - bookId  │
              │ - author │  │ - status  │
              │ - isbn   │  └───────────┘
              └──────────┘
```

### Entity Definitions

#### Book (Value Object)

**Purpose:** Immutable metadata representing a book title

**Characteristics:**
- Immutable after creation
- Identity based on `bookId`
- No state management
- Safe for use in collections

**Fields:**
```java
private final String bookId;    // Unique identifier
private final String title;     // Book title
private final String author;    // Author name
private final String isbn;      // ISBN (optional)
```

**Why Value Object:**
- Book information doesn't change
- Multiple copies share same metadata
- No lifecycle beyond creation
- Equality based on bookId

---

#### BookCopy (Entity)

**Purpose:** Represents a single physical copy with state

**Characteristics:**
- Mutable state (AVAILABLE ↔ ISSUED)
- Unique copyId identity
- Enforces valid state transitions
- Belongs to exactly one Book

**Fields:**
```java
private final String copyId;       // Unique copy identifier
private final String bookId;       // Reference to Book
private CopyStatus status;         // Current state
```

**State Machine:**
```
    ┌──────────────┐
    │  AVAILABLE   │
    └──────┬───────┘
           │ issue()
           ▼
    ┌──────────────┐
    │   ISSUED     │
    └──────┬───────┘
           │ returnCopy()
           ▼
    ┌──────────────┐
    │  AVAILABLE   │
    └──────────────┘
```

**Why Separate from Book:**
- Multiple copies of same book have independent states
- One copy issued, another available
- Issue/return operations target specific physical copies
- Models real-world library behavior accurately

---

#### User (Entity)

**Purpose:** Library member who borrows books

**Characteristics:**
- Tracks borrowed copies
- Enforces borrowing limit
- Cannot modify inventory directly
- Owns responsibility for borrowed items

**Fields:**
```java
private final String userId;              // Unique identifier
private final String name;                // User name
private final Set<BookCopy> borrowedCopies; // Currently borrowed
private static final int MAX_BORROW_LIMIT = 5;
```

**Responsibilities:**
- ✅ Track which copies user has
- ✅ Validate borrowing limit
- ✅ Add/remove from borrowed set
- ❌ Allocate copies from inventory
- ❌ Change copy state
- ❌ Access inventory directly

**Design Principle:**
User is responsible for **tracking**, not **allocation**.

---

#### BookInventory (Aggregate Root)

**Purpose:** Owns and manages all books and copies

**Characteristics:**
- Aggregate root for book domain
- Maintains consistency boundaries
- Single source of truth for availability
- Enforces business invariants

**Data Structures:**
```java
private final Map<String, Book> books;
// Key: bookId → Value: Book metadata

private final Map<String, Set<BookCopy>> allCopies;
// Key: bookId → Value: All copies of that book

private final Map<String, Deque<BookCopy>> availableCopies;
// Key: bookId → Value: Queue of available copies (FIFO)
```

**Operations:**

| Operation | Description | Validation |
|-----------|-------------|------------|
| `addBook()` | Register new book | Duplicate check |
| `addCopies()` | Add physical copies | Book must exist |
| `removeBook()` | Remove if not issued | All copies available |
| `allocateCopy()` | Issue one copy | Availability check |
| `releaseCopy()` | Return copy | State validation |

**Why Aggregate:**
- Ensures total copies ≥ available copies
- Prevents inconsistent state (e.g., negative availability)
- Centralized validation logic
- Clear ownership of book lifecycle

---

#### Library (Orchestrator)

**Purpose:** Coordinates workflows, ensures atomicity

**Characteristics:**
- Application service layer
- Owns user registry
- Delegates to inventory
- Handles rollback on failures

**Responsibilities:**
```
┌─────────────────────────────────────┐
│           Library                   │
│                                     │
│  ┌────────────────────────────┐    │
│  │  User Management           │    │
│  │  - registerUser()          │    │
│  │  - validateUser()          │    │
│  └────────────────────────────┘    │
│                                     │
│  ┌────────────────────────────┐    │
│  │  Workflow Orchestration    │    │
│  │  - borrowBook()            │    │
│  │  - returnBook()            │    │
│  │  - Transaction coordination│    │
│  │  - Rollback handling       │    │
│  └────────────────────────────┘    │
└─────────────────────────────────────┘
```

**Transaction Pattern:**
```java
public BookCopy borrowBook(String userId, String bookId) {
    User user = validateUser(userId);
    
    BookCopy copy = inventory.allocateCopy(bookId);
    
    try {
        user.borrowCopy(copy);
        return copy;
    } catch (RuntimeException e) {
        inventory.releaseCopy(copy); // ROLLBACK
        throw e;
    }
}
```

**Why Orchestrator:**
- User and Inventory should not know about each other
- Centralized transaction management
- Single point for workflow logic
- Easier to test and modify

---

## Design Decisions

### Critical Design Choice: Book vs BookCopy Separation

**Problem:**
How do we model books with multiple copies?

**❌ Wrong Approach:**
```java
class Book {
    private int totalCopies;
    private int availableCopies;
}
```

**Issues:**
- Cannot track which specific copy a user has
- Cannot have copy-specific state (damaged, lost)
- Cannot implement proper return validation
- Doesn't model reality

**✅ Correct Approach:**
```java
class Book {
    // Only metadata
}

class BookCopy {
    private String copyId;
    private String bookId;
    private CopyStatus status;
}
```

**Benefits:**
- Each copy has independent lifecycle
- Can track "User U1 has Copy C42"
- Can add copy-specific attributes (condition, location)
- Models real library behavior
- Validates returns properly

---

### Data Structure Justifications

#### HashMap for Registries

**Choice:** `HashMap<String, Book>` and `HashMap<String, User>`

**Justification:**
- **O(1) average lookup**: Essential for frequent access
- **Identity-based**: Natural fit for entity retrieval
- **Mutation-safe**: We control the lifecycle
- **Standard practice**: Industry-standard choice

**Alternatives Considered:**
- TreeMap: Unnecessary O(log n), no sorting needed
- ArrayList: O(n) lookup, unacceptable
- ConcurrentHashMap: Overkill for single-threaded

---

#### Deque for Available Copies

**Choice:** `Deque<BookCopy>` for available copies queue

**Justification:**
- **FIFO allocation**: First available copy issued first
- **O(1) operations**: `removeFirst()` and `addLast()`
- **Natural semantics**: Queue behavior matches use case
- **Circulation pattern**: Models real-world copy rotation

**Code Pattern:**
```java
// Allocation
BookCopy copy = availableCopies.get(bookId).removeFirst();

// Return
availableCopies.get(bookId).addLast(copy);
```

**Why Not:**
- **ArrayList**: O(n) removal from front
- **Stack**: LIFO doesn't match library behavior
- **PriorityQueue**: No priority needed, adds complexity

---

#### HashSet for Borrowed Copies

**Choice:** `HashSet<BookCopy>` inside User

**Justification:**
- **Uniqueness guarantee**: User can't borrow same copy twice
- **O(1) contains()**: Fast validation on return
- **O(1) add/remove**: Efficient borrow/return
- **No ordering needed**: Borrowed order irrelevant

**Usage:**
```java
// Check ownership
if (!user.getBorrowedCopies().contains(copy)) {
    throw new InvalidReturnException();
}

// Add on borrow
borrowedCopies.add(copy);

// Remove on return
borrowedCopies.remove(copy);
```

---

### Exception Hierarchy Design

**Philosophy:** Fail-fast with explicit domain exceptions

**Hierarchy:**
```
RuntimeException
    └── LibraryException (abstract)
        ├── BookNotFoundException
        ├── UserNotFoundException
        ├── BookUnavailableException
        ├── BorrowLimitExceededException
        ├── InvalidReturnException
        ├── BookIssuedException
        └── DuplicateEntityException
```

**Why RuntimeException:**
- Domain violations are programming errors
- Not recoverable by caller
- Fail-fast principle
- Clean method signatures (no throws clause)

**Exception Guidelines:**
| Scenario | Exception | When to Throw |
|----------|-----------|---------------|
| Entity not found | `EntityNotFoundException` | Lookup fails |
| Rule violation | `BorrowLimitExceededException` | Business rule broken |
| Invalid state | `InvalidReturnException` | Precondition not met |
| Conflict | `DuplicateEntityException` | Unique constraint violated |

---

## Implementation Details

### Package Structure

```
com.library.lld
├── model
│   ├── Book.java
│   ├── BookCopy.java
│   ├── User.java
│   └── BookInventory.java
├── enums
│   └── CopyStatus.java
├── exception
│   ├── LibraryException.java
│   ├── BookNotFoundException.java
│   ├── UserNotFoundException.java
│   ├── BookUnavailableException.java
│   ├── BorrowLimitExceededException.java
│   ├── InvalidReturnException.java
│   ├── BookIssuedException.java
│   └── DuplicateEntityException.java
├── service
│   └── Library.java
└── Main.java
```

### Code Quality Standards

**Immutability:**
```java
public final class Book {
    private final String bookId;
    private final String title;
    private final String author;
    private final String isbn;
    
    // No setters
    // Defensive copies where needed
}
```

**Encapsulation:**
```java
public class User {
    private final Set<BookCopy> borrowedCopies = new HashSet<>();
    
    // Return defensive copy
    public Set<BookCopy> getBorrowedCopies() {
        return new HashSet<>(borrowedCopies);
    }
}
```

**Validation:**
```java
public void borrowCopy(BookCopy copy) {
    if (borrowedCopies.size() >= MAX_BORROW_LIMIT) {
        throw new BorrowLimitExceededException(
            "User " + userId + " has reached maximum borrow limit"
        );
    }
    borrowedCopies.add(copy);
}
```

---

## Error Handling Strategy

### Validation Layers

**Layer 1: Input Validation (Library)**
```java
if (userId == null || userId.isBlank()) {
    throw new IllegalArgumentException("User ID cannot be null or empty");
}
```

**Layer 2: Business Rule Validation (Domain)**
```java
if (!user.canBorrowMore()) {
    throw new BorrowLimitExceededException();
}
```

**Layer 3: State Validation (Entity)**
```java
public void issue() {
    if (status != CopyStatus.AVAILABLE) {
        throw new IllegalStateException("Copy already issued");
    }
    this.status = CopyStatus.ISSUED;
}
```

### Transaction Rollback Pattern

```java
public BookCopy borrowBook(String userId, String bookId) {
    // Step 1: Get user (may throw UserNotFoundException)
    User user = users.get(userId);
    if (user == null) {
        throw new UserNotFoundException(userId);
    }
    
    // Step 2: Allocate copy (may throw BookUnavailableException)
    BookCopy copy = inventory.allocateCopy(bookId);
    
    // Step 3: Assign to user (may throw BorrowLimitExceededException)
    try {
        user.borrowCopy(copy);
        return copy;
    } catch (RuntimeException e) {
        // CRITICAL: Rollback inventory allocation
        inventory.releaseCopy(copy);
        throw e; // Re-throw to caller
    }
}
```

**Why This Matters:**
- Ensures atomicity (all-or-nothing)
- Prevents inconsistent state
- No "phantom" allocations
- Interview favorite topic

---

## Runtime Behavior

### Sequence: Successful Borrow

```
┌──────┐          ┌─────────┐          ┌──────────────┐          ┌──────┐          ┌──────────┐
│Client│          │ Library │          │BookInventory │          │ User │          │BookCopy  │
└───┬──┘          └────┬────┘          └──────┬───────┘          └───┬──┘          └────┬─────┘
    │                  │                      │                      │                   │
    │ borrowBook()     │                      │                      │                   │
    │─────────────────>│                      │                      │                   │
    │                  │                      │                      │                   │
    │                  │ validateUser()       │                      │                   │
    │                  │──┐                   │                      │                   │
    │                  │<─┘                   │                      │                   │
    │                  │                      │                      │                   │
    │                  │ allocateCopy(bookId) │                      │                   │
    │                  │─────────────────────>│                      │                   │
    │                  │                      │                      │                   │
    │                  │                      │ checkAvailability()  │                   │
    │                  │                      │──┐                   │                   │
    │                  │                      │<─┘                   │                   │
    │                  │                      │                      │                   │
    │                  │                      │ dequeue copy         │                   │
    │                  │                      │──┐                   │                   │
    │                  │                      │<─┘                   │                   │
    │                  │                      │                      │                   │
    │                  │                      │                      │      issue()      │
    │                  │                      │──────────────────────┼──────────────────>│
    │                  │                      │                      │                   │
    │                  │                      │                      │      status=ISSUED│
    │                  │                      │                      │                   │──┐
    │                  │                      │                      │                   │<─┘
    │                  │                      │                      │                   │
    │                  │      return copy     │                      │                   │
    │                  │<─────────────────────│                      │                   │
    │                  │                      │                      │                   │
    │                  │ borrowCopy(copy)     │                      │                   │
    │                  │──────────────────────┼─────────────────────>│                   │
    │                  │                      │                      │                   │
    │                  │                      │                      │ validateLimit()   │
    │                  │                      │                      │──┐                │
    │                  │                      │                      │<─┘                │
    │                  │                      │                      │                   │
    │                  │                      │                      │ add to borrowed   │
    │                  │                      │                      │──┐                │
    │                  │                      │                      │<─┘                │
    │                  │                      │                      │                   │
    │                  │      success         │                      │                   │
    │                  │<─────────────────────┼──────────────────────│                   │
    │                  │                      │                      │                   │
    │   return copy    │                      │                      │                   │
    │<─────────────────│                      │                      │                   │
```

### Sequence: Borrow with Rollback

```
┌──────┐          ┌─────────┐          ┌──────────────┐          ┌──────┐          ┌──────────┐
│Client│          │ Library │          │BookInventory │          │ User │          │BookCopy  │
└───┬──┘          └────┬────┘          └──────┬───────┘          └───┬──┘          └────┬─────┘
    │                  │                      │                      │                   │
    │ borrowBook()     │                      │                      │                   │
    │─────────────────>│                      │                      │                   │
    │                  │                      │                      │                   │
    │                  │ allocateCopy()       │                      │                   │
    │                  │─────────────────────>│                      │                   │
    │                  │                      │                      │                   │
    │                  │      return copy     │                      │                   │
    │                  │<─────────────────────│                      │                   │
    │                  │                      │                      │                   │
    │                  │ borrowCopy(copy)     │                      │                   │
    │                  │──────────────────────┼─────────────────────>│                   │
    │                  │                      │                      │                   │
    │                  │                      │                      │ validateLimit()   │
    │                  │                      │                      │──┐                │
    │                  │                      │                      │<─┘                │
    │                  │                      │                      │                   │
    │                  │                      │                      │ ❌ LIMIT EXCEEDED │
    │                  │                      │                      │                   │
    │                  │  ❌ BorrowLimitExceededException           │                   │
    │                  │<─────────────────────┼──────────────────────│                   │
    │                  │                      │                      │                   │
    │                  │ catch exception      │                      │                   │
    │                  │──┐                   │                      │                   │
    │                  │<─┘                   │                      │                   │
    │                  │                      │                      │                   │
    │                  │ releaseCopy(copy)    │                      │                   │
    │                  │─────────────────────>│                      │                   │
    │                  │                      │                      │                   │
    │                  │                      │                      │      returnCopy() │
    │                  │                      │──────────────────────┼──────────────────>│
    │                  │                      │                      │                   │
    │                  │                      │                      │   status=AVAILABLE│
    │                  │                      │                      │                   │──┐
    │                  │                      │                      │                   │<─┘
    │                  │                      │                      │                   │
    │                  │                      │ re-queue copy        │                   │
    │                  │                      │──┐                   │                   │
    │                  │                      │<─┘                   │                   │
    │                  │                      │                      │                   │
    │                  │ rollback complete    │                      │                   │
    │                  │<─────────────────────│                      │                   │
    │                  │                      │                      │                   │
    │                  │ rethrow exception    │                      │                   │
    │                  │──┐                   │                      │                   │
    │                  │<─┘                   │                      │                   │
    │                  │                      │                      │                   │
    │ ❌ Exception     │                      │                      │                   │
    │<─────────────────│                      │                      │                   │
```

**Key Observations:**
1. Inventory allocated copy successfully
2. User validation failed (limit exceeded)
3. Library caught exception
4. Library rolled back inventory allocation
5. Copy returned to available queue
6. Exception re-thrown to client
7. **System remains consistent**

---
