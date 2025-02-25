#include <Arduino.h>
#include "DHT.h"
#include <Stepper.h>
#if defined(ESP32)
  #include <WiFi.h>
#elif defined(ESP8266)
#include <ESP8266WiFi.h>
#endif
#include <Firebase_ESP_Client.h>

#include "addons/TokenHelper.h"
#include "addons/RTDBHelper.h"

//#define WIFI_SSID "MEO-250BCD"
//#define WIFI_PASSWORD "0071F7FF8B"


#define WIFI_SSID "Labs-LSD"
#define WIFI_PASSWORD "aulaslsd"


#define API_KEY "AIzaSyAGC8FEQ0K9qsYKOnWw1KM5oTQoJ68sAWY"
#define DATABASE_URL "https://iot-alarm-app-165dc-default-rtdb.europe-west1.firebasedatabase.app/"

#define STEPS 200 
#define PWM D1

#define DPIN D4        //Pin to connect DHT sensor (GPIO number) D2
#define DTYPE DHT11   // Define DHT 11 or DHT22 sensor type


// Define pins for the motor coils
const int motorPin1 = D5;
const int motorPin2 = D6;
const int motorPin3 = D7;
const int motorPin4 = D8;
Stepper myStepper(STEPS, motorPin1, motorPin3, motorPin2, motorPin4);

DHT dht(DPIN,DTYPE);

FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;
volatile int stpe=STEPS;
bool signupOK = false;

void setup() {
  Serial.begin(9600);
  pinMode(PWM,OUTPUT);
  digitalWrite(PWM,LOW);
  myStepper.setSpeed(38); //1 rpm
  dht.begin();
  

  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to Wi-Fi");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(300);
  }
  Serial.println();
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());
  Serial.println();

  config.api_key = API_KEY;
  config.database_url = DATABASE_URL;

  if (Firebase.signUp(&config, &auth, "", "")) {
    Serial.println("ok");
    signupOK = true;
  } else {
    Serial.printf("%s\n", config.signer.signupError.message.c_str());
  }

  config.token_status_callback = tokenStatusCallback;

  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);
}

void loop() {
  int cnt=0;
  int the, ang;
  int hum = analogRead(A0);
  float tc = dht.readTemperature(false);  //Read temperature in C
  float hu = dht.readHumidity();
  Serial.print("Moisture Sensor Value:");
  Serial.println(hum);
  Serial.print("Temp: ");
  Serial.print(tc);
   Serial.print(" F, Hum: ");
  Serial.print(hu);
  Serial.println("%");
  Firebase.RTDB.setFloat(&fbdo, "S_hum", hum);
  Firebase.RTDB.setFloat(&fbdo, "A_hum", hu);
  Firebase.RTDB.setFloat(&fbdo, "temp", tc);
  Firebase.RTDB.getInt(&fbdo, "th");
  the = fbdo.intData();
  Firebase.RTDB.getInt(&fbdo, "motor");
  ang = fbdo.intData();
  
  if(hum<the)
  {
    Firebase.RTDB.setBool(&fbdo, "Pump", true);
    Firebase.RTDB.setBool(&fbdo, "motor", true);
    digitalWrite(PWM, HIGH);
  }else
  {
    digitalWrite(PWM, LOW);
    Firebase.RTDB.setBool(&fbdo, "Pump", false);
    Firebase.RTDB.setBool(&fbdo, "motor", false);  
  }
  while (hum<the)
  {
    yield();
    cnt++;
    if (cnt>10)
    {
      stpe=STEPS;
      cnt=0;
      stpe=stpe+ang;
    }
    else if (cnt>5)
    {
      stpe=stpe-ang;
    }
    yield();
    hum = analogRead(A0);
    Serial.print("Moisture Sensor Value:");
    Serial.println(hum);
    
    myStepper.step(stpe);
  }
  delay(100); // Adjust delay as needed
}
