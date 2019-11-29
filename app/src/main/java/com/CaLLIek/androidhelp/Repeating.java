package com.CaLLIek.androidhelp;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public class Repeating {
    static MyAsyncTask myAsyncTask;
    public Repeating(View view) {
        final Timer myTimer = new Timer();
        //Handler - это механизм, который позволяет работать с очередью сообщений.
        final Handler uiHandler = new Handler();
        final TextView txtResult = view.findViewById(R.id.bottom);

        myTimer.schedule(new TimerTask() { // Определяем задачу
            @Override
            public void run() {
                if (myAsyncTask != null) {
                    myAsyncTask.cancel(true);
                }
                myAsyncTask = new MyAsyncTask();
                myAsyncTask.execute();

                //получить доступ к ui программы
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                            txtResult.setText("Ошибка выберите меньше API");
                    }
                });

                long answer = (long) -1;
                try {
                    answer = myAsyncTask.get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (answer == -1) {
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            showMessage();
                        }
                    });

                    myTimer.cancel();
                }
            }

            ;
        }, 1000L, 20L * 1000); // интервал - 20000 миллисекунд,  миллисекунд до первого запуска.
    }

    public void showMessage()
    {
        //Toast.makeText(this,"Запуск в автономном режиме",Toast.LENGTH_SHORT).show();
    }
}

class MyAsyncTask extends AsyncTask<String, Integer, Long> {
    String urlString = "http://media.itmo.ru/api_get_current_song.php";
    String login = "4707login";
    String password = "4707pass";

    @Override
    protected Long doInBackground(String... params) {
        return requestHttp();
    }

    private long requestHttp() {
        int answer = -1;

        String data = null;
        try {
            data = URLEncoder.encode("login", "UTF-8")
                    + "=" + URLEncoder.encode(login, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            data += "&" + URLEncoder.encode("password", "UTF-8")
                    + "=" + URLEncoder.encode(password, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String text = "";
        BufferedReader reader = null;
        // Send data
        try {
            // Устанавливаем url куда будем обращаться
            URL url = new URL(urlString);

            // Отправка ПОСТ запроса
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            // Получения ответа от сервера
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line = null;

            // Читаем ответ с сервера
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }


            text = sb.toString();
            answer = 1;
        } catch (Exception ex) {
            //Если произошла ошибка устанавливаем код ошибки
            answer = -1;
        } finally {
            try {
                reader.close();
            } catch (Exception ex) {
            }
        }

        try {
            Log.e("text","t"+text);
            if(text!=null)
            {//Парсим данные из полученого JSON
                JSONObject jObject = new JSONObject(text);
                String result = jObject.getString("result");
                String info = jObject.getString("info");
                String parst[] = info.split(" - ");
                //Track track = new Track(parst[0], parst[1]);


                //Если запрос прошел успешно
                /*
                if(result.equals("success"))
                    if (MainActivity.mainActivity.track == null ||
                            !track.trackName.equals(MainActivity.mainActivity.track.trackName)
                    ) {
                        //добавляем запись в бд и обновляем последний трек
                        track.insert();
                        MainActivity.mainActivity.track = track;
                    };
            */
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Log.e("Ответ с сервера",""+text);
        return (long) answer;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }
}