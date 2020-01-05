# ScheduleCheck
AKA: AspenCheck, AspenInfo, X2Info

ScheduleCheck is a program that gets information (such as schedule information) from Aspen. It is currently used as the backend for the Melrose High School's [AspenDash](https://aspen.studenttech.tk). 

Any district using Aspen can use several features of ScheduleCheck without needing a specialized configuration, as described below.

## Endpoints:
All endpoints are prefixed with '/api/v1/{district-id}' where `district-id` is the ID of your Aspen instance; all endpoints return a JSON object. Pass `ASPEN_UNAME` and `ASPEN_PASS` as header to get personalized results.

* `/aspen`: All endpoints involving aspen
  * `/schedule`: Returns schedule information, such as day, block, etc. (config / auth optional)
  * `/student`: Returns grade, ID, etc regarding the student (auth required)
  * `/course`: Index of enrolled courses, their grades, teachers, etc (auth required)
    * `/{course-id}/assignment`: Gets a list of all of a course's assignments and their grades

* `/announcements`: Returns all announcements scheduled to run (sources gathered from config)
