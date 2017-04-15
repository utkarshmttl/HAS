// push_buttons
int p1 = 7;
int p2 = 8;
int p3 = 9;

// relays
int r1 = 6;
int r2 = 5;
int r3 = 4;

// Data coming from Bluetooth
String data;

// previous states of push_buttons
int prev1 = 0;
int prev2 = 0;
int prev3 = 0;

void setup() {
  Serial.begin(9600);
  pinMode(p1, OUTPUT);
  pinMode(p2, OUTPUT);
  pinMode(p3, OUTPUT);
  pinMode(r1, OUTPUT);
  pinMode(r2, OUTPUT);
  pinMode(r3, OUTPUT);
}


void loop() {
  
  // If push_button1 is pressed
  if(digitalRead(p1)){
    // If previous state of push_button was "not pressed"
    if(!prev1){
      relayStateChange(r1);
      // Set previous state to "pressed"
      prev1 = 1;
    }
  } else{
      if(prev1){
        prev1=0;
      }
  }
    
  if(digitalRead(p2)){
    if(!prev2){
      relayStateChange(r2);
      prev2 = 1;
    }
  } else{
      if(prev2){
        prev2=0;
      }
  }
  
  if(digitalRead(p3)){
    if(!prev3){
      relayStateChange(r3);
      prev3 = 1;
    }
  } else{
      if(prev3){
        prev3=0;
      }
  }
  
  // Checking if data available from Bluetooth
  if (Serial.available() >0) {
    data = Serial.readString();
  } 
  if(data == "11"){
    if(!prev1){
      setRelayState(r1, 0);
    }
  }else if(data == "10"){
    if(!prev1){
//      setRelayState(r1, 1);
    setRelayState(r1, 1);
    }
  }else if(data == "21"){
    if(!prev2){
      setRelayState(r2, 0);
    }
  }else if(data == "20"){
    if(!prev2){
      setRelayState(r2, 1);
    }
  }else if(data == "31"){
    if(!prev3){
      setRelayState(r3, LOW);
    }
  }else if(data == "30"){
    if(!prev3){
      setRelayState(r3, HIGH);
    }
  }else if(data == "41"){
    if(!prev1 && !prev2 && !prev3){
      changeStateOfAll(LOW);
    }
  }else if(data == "40"){
    if(!prev1 && !prev2 && !prev3){
      changeStateOfAll(HIGH);
    }
  } 
  // Reset Data
  data = "";
}

// Function to switch state of a relay
void relayStateChange(int relay_number){
  digitalWrite(relay_number, !digitalRead(relay_number));
}

// Function to set relay state to a specific state (value)
void setRelayState(int relay_number, int value){
  digitalWrite(relay_number, value);
}

// Function to set state of all relays to given state
void changeStateOfAll(int value){
  digitalWrite(r1, value);
  digitalWrite(r2, value);
  digitalWrite(r3, value);
}
