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

import com.google.firebase.auth.FirebaseAuth;
import com.major.touristguide.R;


public class Interest extends AppCompatActivity {

    private android.support.v7.widget.Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FirebaseAuth auth;
    SharedPreferences sp;
	Firebase reference1;


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest);
        Button btn_doneInterest;
        sp = getSharedPreferences("Interest",MODE_PRIVATE);

        btn_doneInterest = (Button) findViewById(R.id.btn_doneInterest);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Exploraguide");


        auth = FirebaseAuth.getInstance();
		Map<String,String> map1=new ArrayList<>();
		 reference1 = new Firebase("https://tourist-guide-fd1e1.firebaseio.com/user");

        btn_doneInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                                    Intent intent = new Intent(Interest.this, MainHome.class);
                                    startActivity(intent);
                                    sp.edit().putBoolean("logged",true).apply();
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
					map1.put("User UID", FirebaseAuth.getInstance().getCurrentUser().getUid());
					map1.put("categoryList", "1");
					  String test = reference1.push().getKey();
                      reference1.child(test).setValue(map);
					
                }

                else

                break;
				  case R.id.checkbox_gardens:
                if (checked){
					
				}

                else

                 break;
				 
				   case R.id.checkbox_religious:
                if (checked){}

                else

                 break;
				 
				  case R.id.checkbox_malls:
                if (checked){}

                else
                    break;

				  case R.id.checkbox_markets:
                if (checked){}

                else
                break;

			  case R.id.checkbox_museums:
                if (checked){}

                else
                break;
			
			case R.id.checkbox_zoo:
                if (checked){}

                else
                    break;

				case R.id.checkbox_hills:
                if (checked){}

                else
                    break;


				case R.id.checkbox_theme:
                if (checked){}

                else
                    break;

				
				 case R.id.checkbox_beaches:
                if (checked){}

                else
                    break;

				 case R.id.checkbox_adventure:
                if (checked){}

                else
                    break;
				
				 case R.id.checkbox_nationalParks:
                if (checked){}

                else
                    break;
				
				 case R.id.checkbox_wildlife:
                if (checked){}

                else
                    break;
				
			
            case R.id.checkbox_monuments:
                if (checked){}

               else

                   break;
                  
                  
            case R.id.checkbox_casinos:
                if (checked){}

                else
                    break;

				 case R.id.checkbox_historical:
                if (checked){}

                else
                    break;
				
				  case R.id.checkbox_waterfalls:
                if (checked){}

                else
                    break;

            case R.id.checkbox_art:
                if (checked){}

                else
                    break;

          
            case R.id.checkbox_local:
                if (checked){}

                else
                    break;
				
				  case R.id.checkbox_desert:
                if (checked){}

                else
                    break;
				
				  case R.id.checkbox_backwaters:
                if (checked){}

                else
                    break;
				
				  case R.id.checkbox_palaces:
                if (checked){}

                else
                    break;
				
				  case R.id.checkbox_nature:
                if (checked){}

                else
                    break;
				
				  case R.id.checkbox_science:
                if (checked){}

                else
                    break;


           
          
        }
    }
    }


