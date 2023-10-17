# ScheduleCheck
AKA: AspenCheck, AspenInfo, X2Info

ScheduleCheck is a program that gets information (such as schedule information) from Aspen. It is currently used as the backend for the Melrose High School's [AspenDash](https://aspen.herocc.com).

Any district using the Follett Hosted version of Aspen (with domain ending in myfollett.com) can use several features of ScheduleCheck without needing a specialized configuration.
If your district has a custom url, feel free to add it to the [config.json](https://github.com/ClassyCreations/ScheduleCheck/blob/master/src/main/resources/config.json), or make a ticket.

The endpoints are subject to change at any time, and should not be used in production environments.

## Endpoints
We publish OpenAPI / Swagger data. To see our endpoints, visit the [swagger docs](https://aspen-api.herocc.com/api/v1/swagger-ui.html).
