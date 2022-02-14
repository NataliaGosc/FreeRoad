# Let's go Ride
> Aplikacja dla rowerzystów do sprawdzania pogody i wyznaczania tras.
## Spis treści
* [Informacje ogólne](#Informacje-ogólne)
* [Dokumentacja](#dokumentacja)
* [Technologia](#Technologie)
* [Przykład kodu](#przykład-kodu)
* [Funkcjonalności](#Funkcjonalności)
* [Status](#status)
* [Kontakt](#kontakt)
## Informacje ogólne
Aplikacja jest przeznaczona dla osób kochających jazdę na rowerze. Dzięki aplikacji dowiesz się jaka jest aktualnie pogoda, co umożliwi planowanie przejażdżki. Użytkownik może wybrać zaproponowaną przez nas trasę.
Applikacja umożliwia pokazanie aktualnej pogody w Twojej lokalizacji oraz wybranie innej lokalizacji i pokazanie pogody. 
Aplikacja umożliwia pokazanie trasy rowerowej i naszej obecnej lokalizacji.
## Dokumentacja
![Logo aplikacji](./img/logo.png)
![Ekran z pogodą](./img/pogoda.png)
![Zmiana miasta dla pogody](./img/wpisz-miasto.png)
![Mapa](./img/mapa.png)
![Ładowanie](./img/ladowanie.png)
![Ikony](./img/ikony-pogody.jpg)
## Technologie
* Android Studio 4.1.1
* Android SDK 10.0(Q) API Level 29
* OpenWeatherMap API
* Google maps API
* Google directions API
## Przykład kodu
* Zmiana tła w zależności od aktualnej pogody:
```ruby
private static String updateWeatherIcon(int condition)
    {
        if(condition>=0 && condition<=300)
        {
            return "thunderstorm";
        }
        else if(condition>300 && condition<=500)
        {
            return "lightrain";
        }
        else if(condition>=500 && condition<=600)
        {
            return "shower";
        }
        else if(condition>=600 && condition<=700)
        {
            return "snownight";
        }
        else if(condition>=701 && condition<=771)
        {
            return "fog";
        }
         else if(condition>=772 && condition<=800)
        {
            return "overcast";
        }
        else if(condition==800)
        {
            return "sunny";
        }
        else if(condition>=801 && condition<=804)
        {
            return "cloud";
        }
        else if(condition>=900 && condition<=902)
        {
            return "thunderstorm";
        }
        else if(condition==903)
        {
            return "snow";
        }
        if(condition==904)
        {
            return "sunny";
        }
        if(condition>=905 && condition<=1000)
        {
            return "thunderstormnight";
        }

        return "dunno";

    }

    public String getmTemperature() {
        return mTemperature +"°C";
    }

    public String getMicon() {
        return micon;
    }

    public String getMcity() {
        return mcity;
    }

    public String getmWeatherType() {
        return mWeatherType;
    }
}
```
* Załadowanie pogody dla aktualnej lokalizacji:
```ruby
private void getWeatherForCurrentLocation() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationLisner = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                String Latitude = String.valueOf(location.getLatitude());
                String Longitude = String.valueOf(location.getLongitude());
                RequestParams params =new RequestParams();
                params.put("lat",Latitude);
                params.put("lon",Longitude);
                params.put("appid",APP_ID);
                letsdoSomeNetworkig(params);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                //nie mozna uzyskac lokalizacji
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
            return;
        }
        mLocationManager.requestLocationUpdates(Location_Provider, MIN_TIME, MIN_DISTANCE, mLocationLisner);
    }
```
* Załadowanie pogody dla wprowadzonej lokalizacji:
```ruby
private void getWeatherForNewCity(String city)
    {
        RequestParams params=new RequestParams();
        params.put("q",city);
        params.put("appid",APP_ID);
        letsdoSomeNetworkig(params);
    }
    private void letsdoSomeNetworkig(RequestParams params)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(WEATHER_UTL,params,new JsonHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Toast.makeText(WeatherActivity.this,"Data Get Success",Toast.LENGTH_SHORT).show();

                WeatherDataActivity weatherD= WeatherDataActivity.fromJson(response);
                updateUI(weatherD);
                //super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                //super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }
```
* Załadowanie się wprowadzonej trasy rowerowej:
```ruby
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Pobieranie danych o trasie z direction API
        getRoute("Chylonia, Gdynia", "Rezerwat przyrody Cisowa, Gdynia");

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void getRoute(String startRoute, String endRoute){

        //Pobieranie danych o trasie z direction API
        DownloadTask task = new DownloadTask();
        task.execute("https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + startRoute +
                "&destination=" + endRoute +
                //"&waypoints=Łężyce, 84-207" +
                "&mode=bicycling" +
                "&key=AIzaSyDAqU8VuZm3-D8hzdd9Uk_pXrvb9h0skI8");
    }
```
* Pokazanie aktualnej lokalizacji:
```ruby
@Override
            public void onLocationChanged(@NonNull Location location) {
                // Toast.makeText(MapsActivity.this, location.toString(), Toast.LENGTH_LONG).show();
                // Add a marker in myLocation and move the camera
                //mMap.clear();

                //Metoda ustawia marker na naszej lokalizacji
                LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(
                        new MarkerOptions()
                                .position(myLocation)
                                .title("Tutaj jestem")
                                //Change colour of marker
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                );

                //Metoda rysuje trasę na podstawie danych z Jsona
                List<LatLng> points = coordList; // list of latlng
                for (int i = 0; i < points.size() - 1; i++) {
                    LatLng src = points.get(i);
                    LatLng dest = points.get(i + 1);

                    // mMap is the Map Object
                    Polyline line = mMap.addPolyline(
                            new PolylineOptions().add(
                                    new LatLng(src.latitude, src.longitude),
                                    new LatLng(dest.latitude,dest.longitude)
                            ).width(4).color(Color.BLUE).geodesic(true)
                    );
                }

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
            }
```
## Funkcjonalności
1) Pogoda dla aktualnej lokalizacji uzytkowanika: 
* zmiana tła w zależności od pogody 
* pobranie lokalizacji użytkownika do której załadowana jest aktualna pogoda
2) Pogoda dla wprowadzonej lokalizacji przez użytkownika:
* wprowadzenie nazwy miejscowosci przez uzytkownika
* wyswietlenie pogody dla wprowadzonego miasta
3) Mapa dla wprowadzonej trasy:
* wyświetlenie trasy rowerowej
* pokazanie położenia użytkownika
## Kontakt
Aplikację stworzyli:
Natalia Gościnna & Damian Jaszewski
