# ScheduleCheck
AKA: AspenCheck, AspenInfo, X2Info

ScheduleCheck is a program that gets information (such as schedule information) from Aspen. It is currently used as the backend for the Melrose High School's [AspenDash](https://aspen.studenttech.tk). 

Any district using Aspen can use several features of ScheduleCheck without needing a specialized configuration, as described below.

## Endpoints:
All endpoints are prefixed with '/api/vΘ/DISTRICT_ID' where `Θ` is the current API version, and `DISTRICT_ID` is the ID of your Aspen instance; all endpoints return a JSON object

* `/aspen`: All endpoints involving aspen
  * `/schedule`: Returns schedule information, such as day, block, etc. (no config required)

* `/announcements`: Returns all announcements scheduled to run (sources gathered from config)
