# MobiControl dependency fork

PumpX2 is Copyright James Woglom and contributors, under the MIT license in `LICENSE.md`.
Upstream: https://github.com/jwoglom/pumpX2 . This fork retains upstream history and notices.

The MobiControl dependency branch is based on upstream
`dad3eea2a3f6ae1bb1a6fdc6b3eac37f3ac7132b`, not the newer upstream main.
Its 13 library/test modifications preserve the existing MobiControl dependency implementation:
BLE setup/recovery, logging redaction, active-profile lookup and basal-limit request validation.
Local sample-app changes and private bench captures are not included.

Do not upgrade the consuming app automatically. Library changes affecting bolus, cartridge,
pairing, authentication or transport require explicit scoped maintainer approval, unit tests,
and separately documented isolated-bench validation. Do not post patient/device data or keys.

CI builds/tests only; it does not upload libraries, APKs, documentation sites or releases.
Known baseline failures: `DailyStatusHistoryLogTest` and `VersionsAHistoryLogTest` also fail on the
untouched upstream base. They remain visible and must be investigated before CI is considered green.
