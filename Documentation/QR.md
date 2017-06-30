## QR content

battyboost.com/qr?{version}{id}{checksum}

version: 1 alphanumeric character, 0 to z
battery id: 10 alphanumeric characters, random
user id: 11 alphanumeric characters, random
checksum: alphanumeric-chars[CRC32(UTF-8(id)) % alphanumeric-chars.length}]
alphanumeric-chars = {0...9, a .. z}

Example for battery:

battyboost.com/qr?0084y5f0c0yf

Example for user:

battyboost.com/qr?0idj1xi6hi3z7

