# Library Management System – Low Level Design (Correct & Complete)

> **A hands-on LLD project to master Core Java, OOP, Collections, DSA-in-design, and clean system modeling**

---

## 📋 Table of Contents
- [Problem Statement](#problem-statement)
- [Functional Requirements](#functional-requirements)
- [Non-Functional Requirements](#non-functional-requirements)
- [Assumptions](#assumptions)
- [Entity Identification](#entity-identification)
- [Class Responsibilities](#class-responsibilities)
- [Data Structure Selection](#data-structure-selection)
- [Design Patterns](#design-patterns)
- [Edge Cases & Exception Handling](#edge-cases--exception-handling)
- [Implementation Guide](#implementation-guide)
- [Future Extensions](#future-extensions)

---

## 🎯 Problem Statement

Design and implement an **in-memory, console-based Library Management System** in Java that manages books, users, and borrowing operations while enforcing strict domain rules and state consistency.

### This system must:
- Model real-world library entities correctly
- Prevent invalid operations by design
- Maintain strong consistency at all times
- Be extensible without rewriting core logic

### 🚫 This is NOT a CRUD demo
### ✅ This is a domain-driven, object-oriented LLD system

---

## ✅ Functional Requirements

### A. Book Management

1. **Add a book** (metadata) with one or more physical copies
2. **Remove a book** only if no copies are currently issued
3. **Search books by**:
   - Book ID
   - Title
   - Author
4. **View**:
   - Total copies
   - Available copies
   - Issued copies

### B. User Management

5. **Register a user** with a unique ID and user type
6. **View books** currently issued to a user

### C. Core Operations

7. **Issue Book**:
   - Validate book existence
   - Validate availability of at least one copy
   - Validate user borrowing eligibility
   - Allocate exactly one copy

8. **Return Book**:
   - Validate user–copy association
   - Restore copy availability
   - Close issue transaction

---

## 🔧 Non-Functional Requirements

| Category | Requirement |
|----------|-------------|
| **Performance** | O(1) lookup for users, books, and issue records |
| **Consistency** | No partial updates (atomic issue/return) |
| **Concurrency** | Single-threaded |
| **Persistence** | In-memory only |
| **Extensibility** | Easy to add policies, fines, reservations |
| **Maintainability** | SRP, no god classes |

---

## 📝 Assumptions

1. Single library instance
2. In-memory, single-process system
3. **Book ≠ BookCopy** (Critical distinction)
4. Books are issued at **copy level**
5. User types are finite (STUDENT, FACULTY)
6. Borrow limits depend on user type (not hardcoded)
7. No due dates, fines, or reservations initially
8. Fail-fast behavior on invalid operations
9. Immediate consistency (no async behavior)

---

## 🧩 Entity Identification

| Entity | Purpose |
|--------|---------|
| **Book** | Immutable metadata (title, author) |
| **BookCopy** | Physical copy with state |
| **User** | Library member |
| **BookIssueRecord** | Issue–return transaction |
| **Library** | Aggregate root |

---

## 🏗️ Class Responsibilities

### `Library` (Aggregate Root)
- **Responsibility**: Orchestrates all operations
- **Key Concepts**: Transaction management, consistency enforcement
- **Owns**: All registries and policies
- **Does NOT**: Hold domain state directly (delegates to registries)

### `Book`
- **Responsibility**: Immutable metadata
- **Key Concepts**: Value object, Encapsulation
- **Fields**: `bookId`, `title`, `author`, `isbn`
- **Does NOT**: Track availability or copies

### `BookCopy`
- **Responsibility**: Physical copy with state management
- **Key Concepts**: State machine, Encapsulation
- **Fields**: `copyId`, `bookId`, `status` (Enum: AVAILABLE, ISSUED)
- **Operations**: `issue()`, `returnCopy()` with state validation
- **Enforces**: Valid state transitions only

### `User`
- **Responsibility**: Holds user data and tracks issued books
- **Key Concepts**: Encapsulation, Collections
- **Fields**: `userId`, `name`, `userType` (Enum), `issuedBooks` (Set<String>)
- **Operations**: `addIssuedBook()`, `removeIssuedBook()`, `getIssuedCount()`
- **Does NOT**: Contain borrowing rules logic

### `BorrowPolicy` (Interface) ⭐
- **Responsibility**: Determines borrowing eligibility
- **Key Concepts**: Strategy Pattern, OCP
- **Method**: `canBorrow(User user)` → boolean
- **Implementations**:
  - `StudentBorrowPolicy` (max 5 books)
  - `FacultyBorrowPolicy` (max 10 books)

### `BookInventory` (Registry)
- **Responsibility**: Manages book catalog and copies
- **Key Concepts**: SRP, Data ownership
- **Operations**: 
  - `addBook()`, `removeBook()`
  - `getAvailableCopies()`, `allocateCopy()`, `releaseCopy()`

### `UserRegistry` (Registry)
- **Responsibility**: Manages user data
- **Key Concepts**: SRP
- **Operations**: `registerUser()`, `getUser()`, `userExists()`

### `IssueRegistry` (Registry)
- **Responsibility**: Tracks active issue records
- **Key Concepts**: SRP, Transaction tracking
- **Operations**: `recordIssue()`, `recordReturn()`, `getActiveIssue()`

---

## 📊 Data Structure Selection (With Justification)

| Use Case | Data Structure | Why |
|----------|---------------|-----|
| **Book lookup** | `HashMap<String, Book>` | O(1) access by bookId |
| **Copies per book** | `HashMap<String, Set<BookCopy>>` | No duplicate copies, all copies for a book |
| **Available copies** | `HashMap<String, Deque<BookCopy>>` | Fast allocation (FIFO), O(1) poll/offer |
| **Users** | `HashMap<String, User>` | Identity lookup O(1) |
| **User's issued books** | `HashSet<String>` (inside User) | Uniqueness, fast contains() |
| **Active issues** | `HashMap<String, BookIssueRecord>` | Key: copyId, O(1) return validation |
| **Issue history** | `ArrayList<BookIssueRecord>` | Append-only, chronological order |

### Key Design Decisions:

**Why `Deque<BookCopy>` for available copies?**
- Efficient allocation: `pollFirst()` → O(1)
- Efficient return: `offerLast()` → O(1)
- Natural FIFO behavior for copy distribution

**Why separate `Set<BookCopy>` and `Deque<BookCopy>`?**
- Set: All copies (total inventory)
- Deque: Only available copies (allocation pool)
- Separation maintains clear state boundaries

**Why `HashMap<String, BookIssueRecord>` keyed by copyId?**
- Return operation needs: "Who has this copy?"
- O(1) validation on return
- Prevents wrong user from returning

---

## 🎨 Design Patterns (Used Judiciously)

### ✅ Strategy Pattern (MANDATORY)

**Used for**: Borrowing rules

```
BorrowPolicy (interface)
 ├── StudentBorrowPolicy (max 5 books)
 └── FacultyBorrowPolicy (max 10 books)
```

**Why this is correct**:
- ✔ Avoids `if (userType == STUDENT)` conditionals
- ✔ Open–Closed Principle compliance
- ✔ Easy to add new user types without modifying Library
- ✔ Interview-correct usage of Strategy

**Implementation**:
```java
interface BorrowPolicy {
    boolean canBorrow(User user);
    int getMaxBooks();
}
```

### ❌ Singleton (Deliberately Avoided)

**Why NOT used**:
- Hard to test
- Unnecessary global state
- Better to inject dependencies

### ❌ State Pattern

**Why NOT needed**:
- Enum-based validation is sufficient
- Only 2 states: AVAILABLE, ISSUED
- State Pattern adds unnecessary complexity

---

## ⚠️ Edge Cases & Exception Handling

### Custom Exception Hierarchy

```
LibraryException (extends RuntimeException)
├── BookNotFoundException
├── UserNotFoundException
├── BookUnavailableException
├── BorrowLimitExceededException
├── InvalidReturnException
└── BookIssuedException
```

### Enforced Scenarios

| Scenario | Protection | Exception |
|----------|-----------|-----------|
| Issue unavailable book | Empty Deque check | `BookUnavailableException` |
| Over-limit borrow | `BorrowPolicy.canBorrow()` | `BorrowLimitExceededException` |
| Return wrong copy | IssueRegistry validation | `InvalidReturnException` |
| Remove issued book | Inventory count check | `BookIssuedException` |
| Duplicate user/book | Registry existence check | `DuplicateEntityException` |
| Non-existent entity | Registry lookup | `EntityNotFoundException` |

### Atomic Operations

**Issue Book Flow**:
1. Validate user exists
2. Validate book exists
3. Check policy eligibility
4. Allocate copy (or fail)
5. Record issue
6. Update user's issued books

**If ANY step fails → No state change (rollback)**

**Return Book Flow**:
1. Validate user exists
2. Validate copy exists in user's records
3. Release copy to available pool
4. Remove from user's issued books
5. Close issue record

---

## 💻 Implementation Guide (Correct Order)

### Phase 1 – Core Entities
1. Create **Enums**: `UserType`, `CopyStatus`
2. Create **Book** (immutable)
3. Create **BookCopy** (with state transitions)
4. Create **User** (with issued books tracking)
5. Create **BookIssueRecord** (transaction object)

### Phase 2 – Registries (SRP)
6. Implement **BookInventory**
7. Implement **UserRegistry**
8. Implement **IssueRegistry**

### Phase 3 – Policies (Strategy)
9. Create `BorrowPolicy` interface
10. Implement `StudentBorrowPolicy`
11. Implement `FacultyBorrowPolicy`

### Phase 4 – Library Orchestration
12. Implement **Library** class
13. Wire registries and policies
14. Implement atomic `issueBook()`
15. Implement atomic `returnBook()`

### Phase 5 – Enhancements
16. Add **Comparators** for sorting books
17. Implement search by title/author
18. Add comprehensive validation

---

## 🚀 Future Extensions (Designed In)

| Feature | How to Add | Pattern/Approach |
|---------|-----------|------------------|
| **Fines** | `FinePolicy` interface | Strategy pattern |
| **Reservations** | `PriorityQueue<BookRequest>` | Queue + Policy |
| **Notifications** | `NotificationService` | Observer pattern |
| **Persistence** | `Repository` abstraction | Repository pattern |
| **Multi-library** | `LibraryManager` | Aggregate pattern |
| **Due dates** | Add `dueDate` to `BookIssueRecord` | Domain enhancement |

### How to Add Fines (OCP Example)

```java
interface FinePolicy {
    double calculateFine(BookIssueRecord record);
}

class PerDayFinePolicy implements FinePolicy {
    public double calculateFine(BookIssueRecord record) {
        // Calculate based on overdue days
    }
}
```

---

## 🎓 What This Project Demonstrates

✅ **Core Java**: Classes, Enums, Exceptions, Constructors, final keyword  
✅ **OOP**: SRP, OCP, Abstraction, Encapsulation, Polymorphism  
✅ **Collections**: HashMap, HashSet, Deque, ArrayList (with justification)  
✅ **DSA inside design**: Hashing (O(1) lookups), Deque operations  
✅ **LLD thinking**: Ownership, invariants, extensibility, atomic operations  
✅ **Design Patterns**: Strategy (correctly applied)  
✅ **Exception Handling**: Custom exceptions, business rule enforcement  

---

## 🔥 Interview Takeaway

### If asked: "Why is this design good?"

**You can answer**:

1. ✅ **Clear ownership** – Each registry owns its data
2. ✅ **No invalid states** – State transitions enforced at entity level
3. ✅ **Behavior isolated via strategies** – Policies are pluggable
4. ✅ **Data structures chosen for access patterns** – HashMap for O(1), Deque for allocation
5. ✅ **Easy to extend without rewriting logic** – OCP compliance via interfaces

### If asked: "Why Strategy Pattern for BorrowPolicy?"

**You can answer**:

- Without it: `if (user.getType() == STUDENT) { max = 5; } else { max = 10; }`
- With it: Each policy encapsulates its own rules
- Adding new user type: Just add new policy class, no changes to Library
- **This is OCP in action**

### If asked: "Why separate Book and BookCopy?"

**You can answer**:

- **Book** = Metadata (title, author) – doesn't change
- **BookCopy** = Physical instance with state (available/issued)
- Same book can have 10 copies with different states
- Issue/return happens at copy level, not book level
- **This models reality correctly**

---

## 📌 Critical Design Principles Applied

1. **Single Responsibility** – Each class has ONE reason to change
2. **Open-Closed** – Open for extension (new policies), closed for modification
3. **Fail-Fast** – Invalid operations throw exceptions immediately
4. **Immutability** – Book metadata never changes
5. **State Encapsulation** – BookCopy manages its own state
6. **Atomic Operations** – Issue/return are all-or-nothing transactions

---

**Next Step**: Start implementing Phase 1! Begin with enums and core entities. �