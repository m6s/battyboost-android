# Trigger functions for Firebase

All paths are relative to `{prefix}/logic/`, where `prefix` will be `"default"` in the production environment.

## Battery borrowing

`calculatePayment(borrowerQR: uuid): integer`

Call before borrowBattery() to show the necessary payment in cents.

`borrowBattery(partnerId: id<partner>, batteryQR: uuid, borrowerQR: uuid, cash: boolean)`

If borrowerQR not assigned, push a new child in `users`.  Add an entry to `userQRs`.
Push a child in `transactions`, add a reference to it from the partner and the user.
Update balance fields for partner and user.


`returnBattery(partnerId: id<partner>, batteryQR: uuid, borrowerQR: uuid, remainingCharge: float)`
`returnBatteryAnonymously(partnerId: id<partner>, batteryQR: uuid, remainingCharge: float)`

Push a child in `transactions`, add a reference to it from the partner and the user.
Update balance fields for partner and user.


`calculateRefund(borrowerQR: uuid): integer`

Call after returnBattery() to show the possible refund in cents.


`linkUserToQR(borrowerQR: uuid)`

Look up user via userQRs, copy to current user and delete.
Update userQRs.

## Partner admin

`addPartner`

`updatePartner`

`deletePartner`
