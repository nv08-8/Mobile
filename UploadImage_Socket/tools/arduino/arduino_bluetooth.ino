#include <SoftwareSerial.h>

// SoftwareSerial(rxPin, txPin)
SoftwareSerial mySerial(10, 11);

#define tb1 2
#define tb2 3

char val;
String statustb1 = "";
String statustb2 = "";

void setup() {
  pinMode(tb1, OUTPUT);
  digitalWrite(tb1, LOW);
  pinMode(tb2, OUTPUT);
  digitalWrite(tb2, LOW);

  mySerial.begin(9600);
  Serial.begin(9600);
}

void loop() {
  // check data serial from bluetooth android App
  if (mySerial.available() > 0) {
    val = mySerial.read();
    Serial.println(val);

    if (val == '1') {
      digitalWrite(tb1, HIGH);
      statustb1 = "1";
    } else if (val == '2') {
      digitalWrite(tb2, HIGH);
      statustb2 = "2";
    } else if (val == 'A') {
      digitalWrite(tb1, LOW);
      statustb1 = "A";
    } else if (val == 'B') {
      digitalWrite(tb2, LOW);
      statustb2 = "B";
    } else if (val == 's') {
      delay(500);
      mySerial.println(statustb1 + statustb2 + "J");
      val = 0;
    }
  }
}

