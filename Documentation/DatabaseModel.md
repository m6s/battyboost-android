# Database model for Firebase

## Intelligent charging stations

```
chargers/
    {chargerId}/
        posId: {posId}
```

`chargers` is for looking up the points of sale when updating the available battery count. A charging station will
notify the backend when a battery is removed or fully charged with the total amount of fully charged batteries.

## Dumb charging stations

```
chargingBatteries/
    {batteryId}/
        chargingCompleteTime: integer
        posId: {posId}
```

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
        area: "berlin"
```

`pos` stands for point of sale and can be queried to show the available batteries on a map.

```
weeklyOpeningHours/
    {posId}/
            openPublicHolidays: boolean (true means maybe)
            mon/
                {id}/
                    from: string (hh:mm)
                    to: string (hh:mm)
                ...
            tue/
                ...
            ...
```

Future: Opening hours per day (`/openingHours/{posId}/{date}/{id}/from|to`)

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
        batteryId: {batteryId}
        partnerId: {partnerId}
        borrowerId: {userId}
        partnerCreditedCents: integer
        borrowerCreditedCents: integer
        cashierId: {userId}
        time: integer
```

```
batteries/
    {batteryId}/
        qr: string, indexed
        manufacturingTime: integer
        chargeCycleCount: integer
        borrowTime: integer
```

## Partners

```
partners/
    {partnerId}/
        name: string
        adminId: {userId}
        posId: {posId}
        balanceCents: integer
```

`posId` only needed for dumb charging stations.

partnerBatteries/
    {partnerId}/
            {batteryId}: true
            ...
partnerTransactions/
    {partnerId}/
            {transactionId}: true
            ...
partnerCashiers/
    {partnerId}/
            {userId}: true
            ...


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
        qr: string, indexed
        earliestBorrowTime: integer
        balanceCents: integer
        bankAccountOwner: string
        iban: string
        photoUrl: string;
        email: string;
        displayName: string;
```

The Admin SDK allows access to the following properties via the FirebaseUser: displayName, email, photoUrl,
providerData, providerId, uid, userMetadata, disabled, emailVerified

`earliestBorrowTime` is the earliest `borrowTime` of all batteries currently borrowed by a user.

userBatteries/
    {userId}/
            {batteryId}: true
            ...
userTransactions/
    {userId}/
            {transactionId}: true
            ...
userPartners/
    {userId}/
            {partnerId} : true
            ...

User's photos are kept in Firebase Storage:

users/{userId}/photo.jpg (JPEG, square format)

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
                    partnerId: {partnerId}
                    userId: {userId}
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
