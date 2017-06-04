# Database model for Firebase

```
chargers/
    {chargerId}/
        partnerId: id<partner>
        posId: id<pos>
```

`chargers` is for looking up the points of sale when updating the available battery count. A charging station will
notify the backend when a battery is removed or fully charged with the total amount of fully charged batteries.

```
pos/
    {posId}/
        type: string
        name: string
        availableBatteryCount: integer
        latitude: double
        longitude: double
```

`pos` stands for point of sale and can be queried to show the available batteries on a map. We may want to use
[GeoFire](https://github.com/firebase/geofire-js) for locations.

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

Transactions allows us and our partners to do accounting.

```
batteries/
    {batteryId}/
        manufacturingTime: integer
        chargeCycleCount: integer
        borrowTime: integer
```

```
partners/
    {partnerId}/
        adminId: id<user>
        balanceCents: integer
        batteries/
            id<battery> : true
            ...
        transactions/
            id<transaction> : true
            ...
		cashiers/
		    id<user> : true
		    ...
```

The `adminId` user can add/remove cashier users.

```
users/
    {userId}/
        oldestBorrowTime: integer
        balanceCents: integer
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

## Writes to logic trigger functions

(See Functions.md for actual function definitions)

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

## Alternative to logic

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
