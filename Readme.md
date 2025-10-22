

## Telemed Consultation Messaging System

> **Author:** Dharma Kevadiya
> Dalhousie University — Master of Applied Computer Science
> [jh692444@dal.ca](mailto:jh692444@dal.ca)
> October 2025



##  Core User Story

* Enable secure doctor–patient consultations with ordered, idempotent text and media messaging for safe and reliable remote care.

---

##  Table of Contents

* [Overview](#-overview)
* [Core Features](#core-features)
* [API Endpoints](#api-endpoints)
* [Data Model](#data-model)
* [System Architecture](#system-architecture)
* [Message Flow](#message-flow)
* [Concurrency & Idempotency](#concurrency--idempotency)
* [Design Patterns](#design-patterns)
* [SOLID Principles](#solid-principles)
* [Future & Production Readiness](#future--production-readiness)
* [Technology Stack](#technology-stack)
* [Trade-offs](#trade-offs)
* [ASCII Overview](#ascii-overview)
* [Testing & Tooling](#testing--tooling)
* [Summary](#summary)

---

##  Overview

A secure and extensible **Telemedicine Consultation Platform** enabling structured doctor–patient messaging (text and media).
The backend uses **Spring Boot 3 + MongoDB**, while the frontend uses **React + TailwindCSS** for a modern chat interface.

---


##  Quick Start

### **Prerequisites**
- [Docker](https://www.docker.com/) and [Docker Compose](https://docs.docker.com/compose/) installed  
- 2–4 GB of free memory  
- Ports **27017**, **8080**, and **5173** (or **80**) available  

---

### **Running the Application**

From the project root, run:

```bash
docker compose up --build
````
**Note:** After running the above command, please wait about **_`1 minute`_** for the seed container to populate demo data into the system.
> The backend and database need a short initialization period before data appears.

This single command will:

* Start a clean **MongoDB** instance (in-memory)
* Build and run the **Spring Boot backend**
* Run the **seed container** to populate demo data
* Launch the **React frontend** served by **Vite** 

Then open the frontend:

* **Frontend:** [http://localhost:5173](http://localhost:5173)
* **Backend API:** [http://localhost:8080](http://localhost:8080)

---

### **Stopping the Application**

To stop and remove containers:

```bash
docker compose down
```

To stop and **clear all data** (Mongo reset):

```bash
docker compose down -v
```

```
```

##  Core Features

* Create and list **consultations** between doctors and patients
* Exchange **messages** (text or media)
* **Idempotent message delivery** to avoid duplicates
* **Atomic message ordering** per consultation
* Simple **file upload API** for media

---

##  API Endpoints

### **1. Create Consultation**

**Endpoint:** `POST /consultations`

**Description:** Creates a new consultation between a patient and a doctor.

**Request Body**

```json
{ "patientId": "P1", "doctorId": "D1" }
```

**Example Response (200 OK)**

```json
{
  "id": "68f5259394e5d61ce5cdb5dd",
  "patientId": "P1",
  "doctorId": "D1",
  "createdAt": "2025-10-19T17:53:23.702485Z"
}
```

**Error Responses**

* 400 – Validation Error

  ```json
  { "error": "VALIDATION_ERROR", "message": "patientId and doctorId are required." }
  ```

---

### **2. Retrieve Consultations**

**Endpoint:** `GET /consultations?patientId={P1}&doctorId={D1}`

**Description:** Retrieves consultations for a participant. At least one parameter is required.

**Query Parameters**

* `patientId` *(optional)* – Filter consultations by patient
* `doctorId` *(optional)* – Filter consultations by doctor

**Example Response (200 OK)**

```json
[
  {
    "id": "68f5259394e5d61ce5cdb5dd",
    "patientId": "P1",
    "doctorId": "D1",
    "createdAt": "2025-10-19T17:53:23.702485Z"
  }
]
```

**Error Responses**

* 400 – Validation Error

  ```json
  { "error": "VALIDATION_ERROR", "message": "Either doctorId or patientId must be provided." }
  ```

---

### **3. Send Message**

**Endpoint:** `POST /consultations/{consultationId}/messages`

**Description:** Adds a new message (text or media) to a consultation.
Ensures idempotency using a unique request header.

**Headers**

* `Idempotency-Key` *(required)* – Unique per message submission

**Text Message Request**

```json
{
  "authorId": "P1",
  "authorRole": "PATIENT",
  "content": { "type": "TEXT", "text": "How are you feeling today?" }
}
```

**Media Message Request**

```json
{
  "authorId": "D1",
  "authorRole": "DOCTOR",
  "content": {
    "type": "MEDIA",
    "storageKey": "local:12345-image.jpg",
    "mimeType": "image/jpeg",
    "sizeBytes": 204800
  }
}
```

**Example Response (200 OK)**

```json
{
  "id": "msg_001",
  "consultationId": "68f5259394e5cdb5dd",
  "authorId": "P1",
  "authorRole": "PATIENT",
  "timestamp": "2025-10-19T18:00:00Z",
  "content": { "type": "TEXT", "text": "How are you feeling today?" }
}
```

**Error Responses**

* 404 – Consultation Not Found

  ```json
  { "error": "NOT_FOUND", "message": "Consultation not found." }
  ```
* 400 – Invalid Author Role

  ```json
  { "error": "INVALID_AUTHOR_ROLE", "message": "Author must be a valid participant." }
  ```
* 400 – Validation Error

  ```json
  { "error": "VALIDATION_ERROR", "message": "Idempotency-Key header is required." }
  ```

---

### **4. Retrieve Messages**

**Endpoint:** `GET /consultations/{consultationId}/messages?role={PATIENT|DOCTOR}`

**Description:** Returns messages in chronological order.
Optional filtering by sender role (`PATIENT` or `DOCTOR`).

**Query Parameters**

* `role` *(optional)* – Filter by sender role

**Example Response (200 OK)**

```json
[
  {
    "sequence": 1,
    "authorRole": "DOCTOR",
    "authorId": "D1",
    "timestamp": "2025-10-19T18:00:12Z",
    "content": { "type": "TEXT", "text": "Please continue your medication." }
  }
]
```

**Error Responses**

* 404 – Consultation Not Found

  ```json
  { "error": "NOT_FOUND", "message": "Consultation not found." }
  ```
* 400 – Invalid Query

  ```json
  { "error": "VALIDATION_ERROR", "message": "role must be PATIENT or DOCTOR when provided." }
  ```

---

### **5. Upload Media File**

**Endpoint:** `POST /media/files`

**Description:** Uploads a media file and returns its storage key for later message reference.

**Request Format**

* `multipart/form-data`

    * Field: `file` *(required)* – Media file to upload

**Example Response (200 OK)**

```json
{
  "storageKey": "local:abc123-image.jpg",
  "sizeBytes": 125003,
  "mimeType": "image/jpeg"
}
```

**Error Responses**

* 400 – Missing File

  ```json
  { "error": "VALIDATION_ERROR", "message": "File is required under form key 'file'." }
  ```

---

##  Data Model

### Consultation

| Field     | Type    | Description            |
| --------- | ------- | ---------------------- |
| id        | String  | Unique consultation ID |
| patientId | String  | Reference to patient   |
| doctorId  | String  | Reference to doctor    |
| createdAt | Instant | Creation timestamp     |

### Message

| Field          | Type    | Description                  |
| -------------- | ------- | ---------------------------- |
| id             | String  | Unique message ID            |
| consultationId | String  | Consultation reference       |
| authorId       | String  | Sender ID                    |
| authorRole     | Enum    | `PATIENT` or `DOCTOR`        |
| content        | Object  | Message payload (text/media) |
| timestamp      | Instant | Message creation time        |
| sequence       | Long    | Incremental message order    |
| idempotencyKey | String  | Prevents duplicates          |

---

##  System Architecture

```
Frontend (React + Tailwind)
│
│-- Axios REST calls with Idempotency-Key
│
└──▶ Backend (Spring Boot)
      ├── Controller Layer (REST + DTO Validation)
      ├── Service Layer (Business Logic)
      │     ├─ Idempotency enforcement
      │     ├─ Sequence generation (atomic counter)
      │     └─ Role-based message validation
      ├── Domain Layer (Entities & Policies)
      └── Infrastructure Layer (MongoDB, FileStorage)
```

### MongoDB Collections

| **Collection**    | **Description**                                                                                  | **Key Indexes**                                                  | **Purpose**                                                                                                                                                              |
| ----------------- | ------------------------------------------------------------------------------------------------ | ---------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **consultations** | Stores doctor–patient consultation records. Each consultation is a unique communication channel. | `{doctorId, createdAt}`, `{patientId, createdAt}`                | Enables fast lookup of consultations for a specific doctor or patient, ordered by creation time (most recent first).                                                     |
| **messages**      | Stores all text and media messages exchanged within consultations.                               | `{consultationId, sequence}`, `{consultationId, idempotencyKey}` | Index on `(consultationId, sequence)` ensures efficient chronological retrieval and pagination; `(consultationId, idempotencyKey)` enforces idempotency to prevent duplicates. |
| **counters**      | Maintains atomic sequence counters per consultation for message ordering.                        | `{_id}`                                                          | Supports consistent message ordering without race conditions, using MongoDB’s atomic `$inc` operation.                                                                   |
| **idempotency**   | Tracks deduplication keys linked to messages.                                                    | `{consultationId, idempotencyKey}` *(unique)*                    | Guarantees exactly-once message delivery by ensuring the same request key cannot insert a duplicate message.                                                             |

---

##  Message Flow

1. **Client** → `POST /consultations/{id}/messages` with unique `Idempotency-Key`.
2. **Service Layer** validates participant and key.
3. If message already exists (idempotency hit) → returns existing message.
4. Otherwise:

    * Increment per-consultation counter
    * Insert new message
    * Link idempotency key to message ID
5. Return message response.

---

##  Concurrency & Idempotency
| **Concern**     | **Approach**                                        | **Purpose**                                                                                                                             |
| --------------- | --------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------- |
| **Ordering**    | Per-consultation atomic counter (`$inc`)            | Guarantees a strict, gap-free sequence per consultation without relying on wall-clock time; avoids race conditions under concurrent writes.  |
| **Duplication** | Unique `(consultationId, idempotencyKey)` index     | Ensures exactly-once semantics on client retries: duplicate submissions with the same key are rejected/returned as the original message.     |
| **Consistency** | Single-document atomic writes                       | Leverages MongoDB’s atomicity at the document level to keep each write operation indivisible, reducing the need for heavyweight transactions. |
| **Clock skew**  | Sequence ordering > timestamps                      | Prevents misordered chats due to client/server time drift; sequence is the source of truth for display and pagination.                       |
| **Isolation**   | Optional transaction upgrade for multi-document ops | Keeps the default path lightweight; transactions are only used when a workflow spans multiple documents and must succeed/fail together.      |

---

##  Design Patterns

| Pattern               | Used In                       | Purpose                             |
| --------------------- |-------------------------------| ----------------------------------- |
| **Repository**        | ConsultationRepo, MessageRepo | Abstraction for persistence         |
| **Builder**           | Message creation              | Fluent object construction          |
| **Factory**           | MessageContentFactory         | Encapsulates TEXT/MEDIA logic       |
| **Strategy**          | MediaStorage (Local)          | Pluggable backend storage           |
| **Adapter**           | Mongo + File integration      | Decouple domain from infrastructure |
| **Specification**     | Message filtering             | Composable query predicates         |
| **Observer (future)** | MessageCreatedEvent           | Enable real-time push               |
| **DTO + Mapper**      | Controller ↔ Domain           | Prevent domain exposure             |
| **DDD-lite**          | Consultation aggregate        | Enforces message policy             |

---

##  SOLID Principles

* **S**ingle Responsibility → Each layer handles one concern
* **O**pen/Closed → Extendable for new content types
* **L**iskov → Swappable storage adapters
* **I**nterface Segregation → Small, focused interfaces
* **D**ependency Inversion → Depend on abstractions, not Mongo/S3

---

##  Future & Production Readiness
| **Category**    | **Enhancements**                                                                                                              | **Purpose**                                                                                                                                                               |
| --------------- | ----------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **Security**    | Implement **JWT authentication**, **role-based access control (RBAC)**, **media type whitelisting**, and **input validation** | Protects sensitive health data by authenticating users and enforcing access policies. Prevents unauthorized uploads and input-based attacks (e.g., SQL/NoSQL injection or XSS). |
| **Performance** | Add **pagination** for large chat histories, **Redis caching** for frequent queries, and **CDN delivery** for media files     | Reduces API response times and server load by caching hot data. CDNs deliver images/videos faster to clients, improving user experience at scale.                              |
| **Reliability** | Use **MongoDB replica sets**, **retryable writes**, and **Spring Boot health checks**                                         | Ensures system uptime and automatic recovery from node failures. Health checks allow proactive monitoring and self-healing deployments.                                        |
| **Scalability** | Introduce **containerization (Docker/Kubernetes)** and **async queues (Kafka/SNS–SQS)** for event-driven operations           | Enables horizontal scaling under high load. Asynchronous messaging offloads heavy tasks (like media processing) without blocking API requests.                                 |
| **Compliance**  | Enforce **HIPAA/PIPEDA compliance**, **encryption-at-rest**, **signed URLs**, and **audit logging**                           | Meets healthcare data protection standards, ensuring all patient information is encrypted, tracked, and securely shared with authorized entities only.                         |
| **Monitoring**  | Integrate **Prometheus metrics**, **ELK stack** (Elasticsearch, Logstash, Kibana), and **alerting pipelines**                 | Provides full observability—tracking request latency, error rates, and uptime. Alerts allow early detection and resolution of performance or security incidents.               |

---

##  Technology Stack
| **Component**   | **Choice**          | **Reason**                                                                                             |
| --------------- | ------------------- | -------------------------------------------------------------------------------------------------------------- |
| **Backend**     | Spring Boot 3       | Provides a robust, modular framework for building secure and maintainable REST APIs with minimal boilerplate.  |
| **Database**    | MongoDB             | Handles flexible JSON-like message documents efficiently without strict schema enforcement, ideal for chat data. |
| **Frontend**    | React + TailwindCSS | Delivers a modern, responsive chat interface with fast rendering and simple styling customization.             |
| **Client HTTP** | Axios               | Simplifies API requests and error handling with a clean, promise-based syntax.                                 |
| **Storage**     | Local File System   | Easy to manage and test during development; files are stored locally with consistent access paths.             |
| **Concurrency** | Atomic Counters     | Ensures ordered message sequencing across concurrent requests using atomic `$inc` operations.                  |
| **Idempotency** | Unique Key + Index  | Guarantees that repeated requests (e.g., due to retries) create only one message, maintaining data integrity.  |


---

##  Trade-offs

| Decision             | Benefit            | Cost                             |
| -------------------- | ------------------ | -------------------------------- |
| REST over WebSockets | Simpler, stateless | No instant updates               |
| MongoDB over SQL     | Flexible schema    | Weaker transactions              |
| Monolith design      | Faster dev         | Limited scalability              |
| Atomic counters      | Reliable ordering  | Slight per-consultation overhead |

---

##  ASCII Overview

```
Client ──(POST /messages + Idempotency-Key)──▶ Controller
   │                                          │
   ▼                                          ▼
 Service ── verifies participant ──> CounterRepo ($inc)
   │                              └> IdempotencyRepo
   ▼
 MessageRepo.insert()
   │
   └──▶ Response → Client (same message if retried)
```

---

##  Testing & Tooling

Use Postman or cURL to test all endpoints.
*A Postman Collection can be generated automatically.*

```bash
curl -X POST http://localhost:8080/consultations \
     -H "Content-Type: application/json" \
     -d '{"patientId":"P1","doctorId":"D1"}'
```
##  Development Notes

**Local Development Topology**

| Component | Port | Description |
|------------|------|-------------|
| **Backend** | `:8080` | Spring Boot REST API |
| **Database** | `:27017` | MongoDB instance |
| **Frontend** | `:5173` or `:80` | React (Vite preview) or static Nginx build |


- **Docker Compose**
    - MongoDB runs with `tmpfs:/data/db` for a clean state on every run.
    - The backend waits for MongoDB health checks before startup.
    - An optional **seed container** can post mock consultations and messages to the API.

- **Idempotency**
    - Clients must send a unique `Idempotency-Key` per message submission.
    - Retries with the same key return the original message to prevent duplication.

- **Message Ordering**
    - Display order is determined by a **per-consultation sequence counter** (`$inc` in Mongo), not timestamps.

- **Media Storage**
    - Local filesystem adapter used for development.
    - Messages reference stored files via a `storageKey` (e.g., `local:filename.jpg`).

- **Validation & Integrity**
    - DTO validation enforces participant roles, message type correctness, and content structure.
    - Invalid payloads or unauthorized participants are rejected early.

- **Health & CORS**
    - Backend exposes `/actuator/health` for container orchestration and seed gating.
    - Enable CORS if the frontend calls the API using absolute URLs (e.g., `http://localhost:8080`).
    - Relative `/api` calls can be proxied via Nginx or Vite’s dev server.

- **Build & Deployment**
    - Frontend built using **Vite**.
    - Backend packaged as a **fat JAR** for simplicity.
    - Docker images use **minimal base layers** to keep the footprint small.

---

## ️ Assumptions Made

- Each consultation involves **exactly one doctor and one patient**.
- Only **authenticated participants** can send or fetch messages in their consultations.
- Each message is **either TEXT or MEDIA**, never both.
- **Server timestamps** are authoritative; clients do not supply creation times.
- **Local filesystem** is sufficient for development media storage.
- **Single-instance deployment** assumed for development (no clustering or distributed locks yet).

---

##  License

This project was created as part of a coding challenge for **PRAXES**.

Thank you for reviewing my submission!  
I focused on creating a clean, working solution that demonstrates **RESTful API design**, **proper data modeling**, and **awareness of production concerns**.

I’m excited about the opportunity to work with the **Praxes team** on innovative healthcare software.
