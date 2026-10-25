Provided Reference API — Listing Search Service (Android)
============================================================

This is a working reference API you can run locally. You are not expected
to write or modify any backend code for this assessment — just run it and
build your native Android app against it.

Requirements
------------
Python 3 (standard library only — no pip installs needed).

Running it
----------
    python3 server.py

By default it listens on http://0.0.0.0:8000 (port can be overridden, e.g.
`python3 server.py 9000`).

Reaching it from your Android app
----------------------------------
"localhost" means something different on a phone or emulator than it does
on your own machine, so pick the right host based on where your app runs:

  - Android emulator: use http://10.0.2.2:8000 — this is the emulator's
    special alias for the host machine running the server. Plain
    "localhost" from inside the emulator points at the emulator itself,
    not your computer, and will fail to connect.

  - Physical device over Wi-Fi: use your computer's LAN IP address, e.g.
    http://192.168.1.42:8000. Make sure the device and your computer are
    on the same network, and that nothing (e.g. a firewall) is blocking
    the port.

Health check
------------
    GET /health
    -> {"status": "ok"}

Search endpoint
----------------
    GET /listings/search

Query parameters (all optional):
    minPrice       number
    maxPrice       number
    minBedrooms    integer
    city           string (exact match, case-insensitive)
    keyword        string (matched against address + description)
    targetBudget   number (used to compute a relevance score)
    page           integer, default 1
    pageSize       integer, default 10

Response shape:
    {
      "results": [ { ...listing fields..., "score": number }, ... ],
      "page": number,
      "pageSize": number,
      "totalResults": number,
      "totalPages": number
    }

Each listing includes: id, source, address, city, state, zip, price,
bedrooms, bathrooms, sqft, latitude, longitude, listedDate, status,
description, plus the computed "score" field.

Errors
------
Invalid input (e.g. minPrice > maxPrice, or a non-positive page/pageSize)
returns a 400 with a JSON body: {"error": "..."}.

A valid request with no matching listings returns a normal 200 with an
empty "results" array, not an error.

Note
----
Your interviewer may point you at additional response fields or another
endpoint later in the session — that's expected, not a bug in what's
documented here.
