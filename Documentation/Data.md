# Database model for Firebase

All paths are relative to `{prefix}/data/`, where `prefix` will be `"default"` in the production environment.

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
        type: "rental" | "return" | "delivery" | "collection"
        batteryId: {batteryId}
        partnerId: {partnerId}
        partnerCreditedCents: integer
        cashierId: {userId}
        conflictTransactionId: {transactionId}
        time: integer
```

```
batteries/
    {batteryId}/
        qr: string, indexed
        rentalTime: integer
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
        rentalTime: integer
        balanceCents: integer
        bankAccountOwner: string
        iban: string
        photoUrl: string;
        email: string;
        displayName: string;
```

The Admin SDK allows access to the following properties via the FirebaseUser: displayName, email, photoUrl,
providerData, providerId, uid, userMetadata, disabled, emailVerified

`rentalTime` is the earliest `rentalTime` of all batteries currently rented by a user.

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
