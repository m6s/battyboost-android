# Database model for Firebase

## Intelligent charging stations

```
chargers/
    {chargerId}/
        posId: id<pos>
```

`chargers` is for looking up the points of sale when updating the available battery count. A charging station will
notify the backend when a battery is removed or fully charged with the total amount of fully charged batteries.

## Dumb charging stations

```
chargingBatteries/
    {batteryId}/
        chargingCompleteTime: integer
        posId: id<pos>
```

## QR assignment

```
userQRs/
    uuid: id<user>
    ...
```

```
batteryQRs/
    uuid: id<battery>
    ...
```

```
allocatedQRs/
    uuid: true
    ...
```

Used to double-check valid QR

## Finding battyboost shops

```
pos/
    {posId}/
        name: string
        info: string
        imageUrl: string
        url: string
        availableBatteryCount: integer
        latitude: double
        longitude: double
        weeklyOpeningHours/
            mon/
                {id}/
                    from: string (hh:mm)
                    to: string (hh:mm)
                ...
            tue/
                ...
            ...
        openPublicHolidays: boolean (true means maybe)
        area: "berlin"
```

`pos` stands for point of sale and can be queried to show the available batteries on a map. We may want to use
[GeoFire](https://github.com/firebase/geofire-js) for locations.

Future: Opening hours per day (`pos/{posId}/openingHours/{date}/{id}/from|to`)

```
publicHolidays/
    berlin/
        string (yyyy-dd-mm): true
        ...
```

## Accounting

```
transactions/
    {transactionId}/
        type: "borrow" | "return"
        batteryId: id<battery>
        partnerId: id<partner>
        borrowerId: id<user>
        partnerCreditedCents: integer
        borrowerCreditedCents: integer
        cashierId: id<user>
        time: integer
```

```
batteries/
    {batteryId}/
        qr: uuid<battery>
        manufacturingTime: integer
        chargeCycleCount: integer
        borrowTime: integer
```

## Partners

```
partners/
    {partnerId}/
        name: string
        adminId: id<user>
        posId: id<pos>
        balanceCents: integer
        batteries/
            id<battery>: true
            ...
        transactions/
            id<transaction>: true
            ...
        cashiers/
            id<user>: true
            ...
```

`posId` only needed for dumb charging stations.

```
invites/
    {partnerId}/
        token: string
        validTime: integer
```

The `adminId` user can add/remove cashier users.

## User management

```
users/
    {userId}/
        qr: uuid<user>
        oldestBorrowTime: integer
        balanceCents: integer
        photoUrl: string
        batteries/
            id<battery> : true
            ...
        transactions/
            id<transaction> : true
            ...
        partners/
            id<partner> : true
            ...
```

`oldestBorrowTime` is the oldest `borrowTime` of all batteries currently borrowed by a user.

## Database triggered functions

All write access is through trigger functions. (See Functions.md for actual function definitions)

```
logic/
    {userId}/
        addPartner/
            {execId}/
                input/
                output/
                    succeeded: boolean
        addCashier/
            {execId}/
                input/
                    partnerId: id<partner>
                    userId: id<user>
                output/
                    succeeded: boolean
```

## Alternative to for database triggered functions

```
command/
    {userId}/
        {execId}/
            name: "addPartner" | "addCashier"
            params...
response/
    {userId}/
        {execId}/
            succeeded: boolean
```
