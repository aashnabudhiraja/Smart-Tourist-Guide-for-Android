package com.major.touristguide.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.major.touristguide.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Interest extends AppCompatActivity {

	Firebase reference1;
    List<String> categoriesList = new ArrayList<>();


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest);
        Button btn_doneInterest;

        btn_doneInterest = (Button) findViewById(R.id.btn_doneInterest);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Exploraguide");


        Firebase.setAndroidContext(this);
		 reference1 = new Firebase("https://tourist-guide-fd1e1.firebaseio.com/user");

        btn_doneInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Categories "+categoriesList);

                reference1.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("numOfTrips").setValue("1");
                reference1.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("categoryList").setValue(categoriesList);
                                    Intent intent = new Intent(Interest.this, MainHome.class);
                                    startActivity(intent);
                                    finish();

                            }
                        });


            }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkbox_lake:
                if (checked){
                    categoriesList.add("1");
					
                }
                break;
				  case R.id.checkbox_gardens:
                if (checked){
                    categoriesList.add("2");
					
				}
                 break;
				 
				   case R.id.checkbox_religious:
                if (checked){
                    categoriesList.add("3");
                }

                 break;
				 
				  case R.id.checkbox_malls:
                if (checked){
                    categoriesList.add("4");
                }

                    break;

				  case R.id.checkbox_markets:
                if (checked){
                    categoriesList.add("5");
                }
                break;

			  case R.id.checkbox_museums:
                if (checked){
                    categoriesList.add("6");
                }

                break;
			
			case R.id.checkbox_zoo:
                if (checked){
                    categoriesList.add("7");
                }

                    break;

				case R.id.checkbox_hills:
                if (checked){
                    categoriesList.add("8");
                }

                    break;


				case R.id.checkbox_theme:
                if (checked){
                    categoriesList.add("9");
                }
                    break;

				
				 case R.id.checkbox_beaches:
                if (checked){
                    categoriesList.add("10");
                }

                    break;

				 case R.id.checkbox_adventure:
                if (checked){
                    categoriesList.add("11");
                }
                    break;
				
				 case R.id.checkbox_nationalParks:
                if (checked){
                    categoriesList.add("12");
                }
                    break;
				
				 case R.id.checkbox_wildlife:
                if (checked){
                    categoriesList.add("13");
                }
                    break;
				
			
            case R.id.checkbox_monuments:
                if (checked){
                    categoriesList.add("14");
                }
                   break;
                  
                  
            case R.id.checkbox_casinos:
                if (checked){
                    categoriesList.add("15");
                }
                    break;

				 case R.id.checkbox_historical:
                if (checked){
                    categoriesList.add("16");
                }
                    break;
				
				  case R.id.checkbox_waterfalls:
                if (checked){
                    categoriesList.add("17");
                }
                    break;

            case R.id.checkbox_art:
                if (checked){
                    categoriesList.add("18");
                }
                    break;

          
            case R.id.checkbox_local:
                if (checked){
                    categoriesList.add("19");
                }
                    break;
				
				  case R.id.checkbox_desert:
                if (checked){
                    categoriesList.add("20");
                }
                    break;
				
				  case R.id.checkbox_backWaters:
                if (checked){
                    categoriesList.add("21");
                }
                    break;
				
				  case R.id.checkbox_palaces:
                if (checked){
                    categoriesList.add("22");
                }
                    break;
				
				  case R.id.checkbox_nature:
                if (checked){
                    categoriesList.add("23");
                }
                    break;
				
				  case R.id.checkbox_science:
                if (checked){
                    categoriesList.add("24");
                }
                    break;
          
        }
    }
    }


