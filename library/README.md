# Library Management System – Low Level Design (LLD)

> **A rigorous, interview-oriented Low Level Design project built using Core Java, OOP principles, and deliberate data structure choices.**

**This project focuses on how to think like a software engineer during LLD, not just how to write code.**

---

## 📌 Project Scope

| Aspect | Detail |
|--------|--------|
| **Type** | In-memory, console-based system |
| **Language** | Java |
| **Focus** | Low Level Design, object modeling, responsibility separation |
| **Storage** | In-memory only |
| **Concurrency** | Single-threaded |

### 🚫 This is NOT a CRUD application
### ✅ This is a domain-driven, object-oriented system

---

## 🎯 Problem Statement

Design and implement an in-memory Library Management System that allows:

- Managing books and their physical copies
- Registering users
- Borrowing and returning books
- Enforcing strict business rules
- Maintaining consistent state at all times

### The design must:
- Reflect real-world library behavior
- Prevent invalid operations by design
- Be extensible without breaking existing code
- Clearly separate who owns what responsibility

---

## ❌ Explicit Non-Goals

To keep the design clean and interview-ready, the system does **NOT** include:

- UI / GUI / Web APIs
- Database or file persistence
- Authentication / authorization
- Concurrency or multi-threading
- Sensors, scanners, or hardware integration
- Fine calculation or due-date logic (future extension)

---

## ✅ Functional Requirements

### A. Book Management

1. **Add a book** with immutable metadata
2. **Add multiple physical copies** of a book
3. **Remove a book** only if no copies are issued
4. **Track**:
   - Total copies
   - Available copies
   - Issued copies

### B. User Management

5. **Register a user** with a unique ID and name
6. **Track which book copies** a user has borrowed
7. **Enforce a maximum borrow limit** per user

### C. Borrow & Return (Core Logic)

8. **Borrow a book**:
   - Validate user exists
   - Validate book exists
   - Validate availability
   - Allocate exactly one copy
   - Update user + inventory consistently

9. **Return a book**:
   - Validate user–copy relationship
   - Restore availability
   - Prevent invalid returns

---

## 🔧 Non-Functional Requirements

| Category | Requirement |
|----------|-------------|
| **Performance** | O(1) lookup for users and books |
| **Consistency** | No partial updates |
| **Atomicity** | Borrow / return is all-or-nothing |
| **Extensibility** | Easy to add policies later |
| **Maintainability** | SRP, no god classes |

---

## 📝 Assumptions

1. Single library instance
2. In-memory system
3. Book metadata is **immutable**
4. **Book ≠ BookCopy** (critical distinction)
5. Books are issued at **copy level**
6. Max borrow limit = **5** (current phase)
7. Fail-fast behavior for invalid operations
8. Immediate consistency (no async behavior)

---

## 🧩 Entity Identification

| Entity | Description |
|--------|-------------|
| **Book** | Immutable metadata |
| **BookCopy** | Physical copy with state |
| **User** | Library member |
| **BookInventory** | Manages books and copies |
| **Library** | Orchestrator (aggregate root) |

---

## 🏗️ Class Responsibilities (As Implemented)

### `Book`
- **Responsibility**: Immutable value object holding metadata
- **Key Principles**: Encapsulation, Immutability, Identity correctness
- **Fields**: `bookId`, `title`, `author`, `isbn`
- **Does NOT**: Track inventory, state, or availability
- **Identity**: Based on `bookId` (proper `equals()` / `hashCode()`)

### `BookCopy`
- **Responsibility**: Represents one physical instance with state
- **Key Principles**: State management, Encapsulation
- **Fields**: `copyId`, `bookId`, `status` (Enum: AVAILABLE, ISSUED)
- **Operations**: `issue()`, `returnCopy()` with state transition validation
- **Why this exists**:
  - Multiple copies of same book behave independently
  - Issue/return happens at **copy level**
  - Each copy has its own lifecycle

### `BookInventory` (Aggregate Root)
- **Responsibility**: Owns all books and all copies
- **Key Principles**: Aggregate pattern, Consistency guardian
- **Owns**:
  - Book registry
  - All physical copies
  - Availability tracking
- **Operations**:
  - `addBook()`, `addCopies()`, `removeBook()`
  - `allocateCopy()` - allocates one available copy
  - `releaseCopy()` - returns copy to available pool
  - Prevents illegal removals (issued copies)
- **Does NOT**:
  - Know about users
  - Handle business workflows directly

### `User`
- **Responsibility**: Tracks borrowed copies and enforces limits
- **Key Principles**: Encapsulation, SRP
- **Fields**: `userId`, `name`, `borrowedCopies` (HashSet<BookCopy>)
- **Operations**: 
  - `borrowCopy()` - adds to borrowed set, validates limit
  - `returnCopy()` - removes from borrowed set
  - `canBorrowMore()` - checks against MAX_BORROW_LIMIT
- **MUST NOT**:
  - Allocate copies from inventory
  - Release copies to inventory
  - Change copy state
  - Know how inventory works

### `Library` (Orchestrator)
- **Responsibility**: Central coordinator ensuring atomic operations
- **Key Principles**: Transaction management, Orchestration
- **Owns**: User registry
- **Coordinates**:
  - User validation
  - Inventory allocation
  - Borrow / return workflows
- **Ensures**:
  - Atomic operations
  - No inconsistent state
  - Proper object interaction
  - **Rollback on failures**
- **Operations**:
  - `registerUser()`
  - `borrowBook(userId, bookId)` - atomic with rollback
  - `returnBook(userId, copy)` - atomic validation

---

## 📊 Data Structure Selection (With Justification)

| Purpose | Data Structure | Reason |
|---------|---------------|--------|
| **Book lookup** | `HashMap<String, Book>` | O(1) access by bookId |
| **User lookup** | `HashMap<String, User>` | O(1) identity lookup |
| **All copies per book** | `HashMap<String, HashSet<BookCopy>>` | Track total inventory, prevent duplicates |
| **Available copies** | `HashMap<String, Deque<BookCopy>>` | O(1) allocate (removeFirst) / return (addLast), FIFO allocation |
| **User borrowed copies** | `HashSet<BookCopy>` (inside User) | Fast contains/remove, uniqueness |

### Why `Deque<BookCopy>` for availability?

✅ **FIFO allocation** - First available copy is issued first  
✅ **O(1) operations** - `removeFirst()` and `addLast()`  
✅ **Models real-world** - Copy circulation pattern  

**All collections were chosen based on responsibility, not convenience.**

---

## 🔁 Transaction Safety (Critical)

### Borrow Operation (Atomic):

```
1. Validate user exists
2. Allocate copy from inventory
3. Assign copy to user
   ❌ If step 3 fails → inventory rollback occurs
```

### Return Operation (Atomic):

```
1. Validate user owns copy
2. Remove from user
3. Release to inventory
```

**No operation leaves the system half-updated.**

---

## ⚠️ Exception Strategy

- **Only RuntimeExceptions are used**
- Represent:
  - Invalid input
  - Business rule violations
  - Illegal state transitions
- **No checked exceptions** in the domain layer

### Exception Hierarchy:

```
LibraryException (extends RuntimeException)
├── BookNotFoundException
├── UserNotFoundException
├── BookUnavailableException
├── BorrowLimitExceededException
├── InvalidReturnException
└── BookIssuedException
```

### Enforced Scenarios:

| Scenario | Exception | When |
|----------|-----------|------|
| Borrow unavailable book | `BookUnavailableException` | Empty available queue |
| Exceed borrow limit | `BorrowLimitExceededException` | User has max copies |
| Return unowned copy | `InvalidReturnException` | Copy not in user's set |
| Remove issued book | `BookIssuedException` | Available ≠ Total copies |
| Duplicate registration | `DuplicateEntityException` | ID already exists |

**Invalid operations fail fast. No silent failures. No partial updates.**

---

## 🔄 Sequence Diagrams (Runtime Object Interaction)

### 1️⃣ Borrow Book Flow (Happy Path)

```
Client
  |
  | borrowBook(userId, bookId)
  |
Library
  |
  | get user from users map
  |
  | allocateCopy(bookId)
  |
BookInventory
  |
  | check book exists
  | check availableCopies not empty
  | remove copy from available queue
  | issue() on BookCopy
  | return BookCopy
  |
Library
  |
  | borrowCopy(copy)
  |
User
  |
  | validate borrow limit
  | add copy to borrowedCopies
  |
Library
  |
  | return BookCopy
  |
Client
```

**Key LLD Takeaways**:
- Library coordinates everything
- Inventory owns allocation
- User owns responsibility
- No object violates SRP

---

### 2️⃣ Borrow Book Flow (Failure with Rollback)

**Scenario**: User exceeds borrowing limit → allocation must be rolled back.

```
Client
  |
  | borrowBook(userId, bookId)
  |
Library
  |
  | allocateCopy(bookId)
  |
BookInventory
  |
  | allocate and issue BookCopy
  | return BookCopy
  |
Library
  |
  | borrowCopy(copy)
  |
User
  |
  | ❌ throw IllegalStateException (limit exceeded)
  |
Library
  |
  | catch RuntimeException
  | releaseCopy(copy)
  |
BookInventory
  |
  | returnCopy() on BookCopy
  | add copy back to available queue
  |
Library
  |
  | rethrow exception
  |
Client
```

**Why This Diagram Is IMPORTANT**:
- Proves atomicity
- Transaction safety
- No partial state updates
- **Interviewers love this**

---

### 3️⃣ Return Book Flow (Happy Path)

```
Client
  |
  | returnBook(userId, copy)
  |
Library
  |
  | get user
  | returnCopy(copy)
  |
User
  |
  | remove copy from borrowedCopies
  |
Library
  |
  | releaseCopy(copy)
  |
BookInventory
  |
  | validate copy belongs to inventory
  | validate state == ISSUED
  | returnCopy() on BookCopy
  | add copy back to available queue
  |
Library
  |
Client
```

**LLD Observations**:
- User returns first → prevents invalid inventory release
- Inventory restores availability
- State transitions are controlled

---

### 4️⃣ Invalid Return Scenario

**Scenario**: User tries to return a book they never borrowed.

```
Client
  |
  | returnBook(userId, copy)
  |
Library
  |
  | returnCopy(copy)
  |
User
  |
  | ❌ throw IllegalStateException (copy not owned)
  |
Library
  |
  | ❌ stop (inventory NOT touched)
  |
Client
```

**Why This Is Correct**:
- Inventory state remains untouched
- No false availability increase
- Strong invariants preserved

---

### 5️⃣ High-Level Interaction Summary

```
Client
   ↓
Library  ←── orchestrates workflows
   ↓
User        BookInventory
   ↓              ↓
Borrow state   Availability state
```

---

## ✅ What Has Been Implemented

✅ **Book** — immutable, identity-safe  
✅ **BookCopy** — state-aware physical copy  
✅ **BookInventory** — aggregate root for books/copies  
✅ **User** — borrowing responsibility with limits  
✅ **Library** — transactional orchestrator with rollback  

**The system is complete, consistent, and interview-ready.**

---

## 🚀 Future Extensions (Designed-In)

Without changing core logic, the system can be extended with:

| Feature | Design Path |
|---------|-------------|
| **Borrowing policies** | Strategy pattern (StudentPolicy, FacultyPolicy) |
| **Reservations** | Queue-based model with PriorityQueue |
| **Due dates & fines** | Add to BookIssueRecord + FinePolicy |
| **Persistence layer** | Repository abstraction |
| **Multiple libraries** | Higher-level aggregate (LibraryManager) |
| **Search by title/author** | Iterate or add inverted index |

---

## 🎓 What This Project Demonstrates

✅ **Core Java mastery** — `final`, enums, exceptions, collections  
✅ **Correct OOP modeling** — Encapsulation, Abstraction, SRP  
✅ **Responsibility ownership** — Clear boundaries, no god classes  
✅ **Data-driven design** — Structures match access patterns  
✅ **LLD interview thinking** — Entity modeling, state management  
✅ **Transaction safety** — Atomic operations with rollback  
✅ **Real-world system modeling** — Book ≠ BookCopy distinction  

---

## 🔥 Interview Takeaway

### If asked: **"Why is this design good?"**

You can say:

1. ✅ **Each class has one clear responsibility** — SRP enforced strictly
2. ✅ **State transitions are tightly controlled** — No invalid states possible
3. ✅ **Inventory consistency is guaranteed** — Aggregate pattern
4. ✅ **Objects do not overstep boundaries** — User can't touch inventory
5. ✅ **Design is extensible without refactoring** — OCP via Strategy
6. ✅ **Data structures match access patterns** — HashMap O(1), Deque O(1)
7. ✅ **Transaction safety with rollback** — All-or-nothing operations

---

### If asked: **"Why Book and BookCopy are separate?"**

You can say:

- **Book** = Metadata (title, author) — doesn't change, doesn't have state
- **BookCopy** = Physical instance with lifecycle (AVAILABLE → ISSUED → AVAILABLE)
- Same book can have 10 copies with **different states**
- Issue/return happens at **copy level**, not book level
- **This models reality correctly** — multiple students can't borrow "the same copy"

---

### If asked: **"How do you ensure atomicity?"**

You can say:

```java
try {
    BookCopy copy = inventory.allocateCopy(bookId);
    user.borrowCopy(copy);
} catch (RuntimeException e) {
    inventory.releaseCopy(copy); // ROLLBACK
    throw e;
}
```

- If user fails to accept, inventory is rolled back
- No partial updates
- System remains consistent

---

### If asked: **"Why Deque for available copies?"**

You can say:

- `removeFirst()` → O(1) allocation (FIFO)
- `addLast()` → O(1) return
- Models real library behavior (first available copy issue