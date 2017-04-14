
int p1 = 7;
int p2 = 8;
int p3 = 9;
int r1 = 6;
int r2 = 5;
int r3 = 4;

char data = 0;

int prev1 = 0;
int prev2 = 0;
int prev3 = 0;


void setup() {                
  
  pinMode(p1, OUTPUT);
  pinMode(p2, OUTPUT);  
  pinMode(p3, OUTPUT);
  pinMode(r1, OUTPUT);  
  pinMode(r2, OUTPUT);  
  pinMode(r3, OUTPUT);  
  Serial.begin(9600);
}


void loop() {
  if(digitalRead(p1)){
    if(!prev1){
      relayStateChange(r1);
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
  
  
  if(Serial.available() > 0)  // Send data only when you receive data:
  {
    data = Serial.read();
    Serial.print(data);  
    Serial.print("\n"); 
   
    
    if(data == '1'){
    if(!prev1){
      relayStateChange(r1);
    }
  }
  
  
    else if(data == '2'){
    if(!prev2){
      relayStateChange(r2);
    }
  }
  
  
  else if(data == '3'){
    if(!prev3){
      relayStateChange(r3);
    }
  }
  
  } 
}

void relayStateChange(int relay_number){
  digitalWrite(relay_number, !digitalRead(relay_number));

}
