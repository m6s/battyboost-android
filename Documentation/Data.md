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
        cashierId: {userId}
        mainPartnerId: {partnerId}
        mainPartnerCreditedCents: integer
        conflictPartnerId: {partnerId}
        conflictPartnerCreditedCents: {partnerId}
        transactionTime: integer
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

The `adminId` user can add/remove cashier users.

```
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

```
invites/
    {partnerId}/
        token: string
        validTime: integer
```

## User management

```
users/
    {userId}/
        bankAccountOwner: string
        iban: string
        photoUrl: string;
        email: string;
        displayName: string;
        cashierPartnerId: {partnerId}
```

User's photos are kept in Firebase Storage:

users/{userId}/photo.jpg (JPEG, square format)

```
batteryUsers/
    {batteryId}/
            {userId}: true
```
