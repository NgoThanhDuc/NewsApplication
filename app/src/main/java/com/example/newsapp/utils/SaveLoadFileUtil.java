package com.example.newsapp.utils;

import android.content.Context;
import android.util.Log;

import com.example.newsapp.models.ChiTietTinTuc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class SaveLoadFileUtil {

    public void saveFileTabSelect(Context context, String tabSelect) {
        File file = new File(context.getFilesDir(), "tabSelected.txt");

        if (!file.exists()) {
            try {
                FileOutputStream fs_out = context.openFileOutput("tabSelected.txt", Context.MODE_PRIVATE);
                OutputStreamWriter os = new OutputStreamWriter(fs_out);
                os.write(tabSelect);
                os.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                FileOutputStream fs_out = context.openFileOutput("tabSelected.txt", Context.MODE_PRIVATE);
                OutputStreamWriter os = new OutputStreamWriter(fs_out);
                os.write(tabSelect);
                os.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String loadFileTabSelect(Context context) {
        String tabSelect = "";

        try {
            InputStream inputStream = context.openFileInput("tabSelected.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                tabSelect = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return tabSelect;
    }

    // save file historyNews and danhDauNews
    public void saveFileNews(Context context, String fileName, String newsName, String title, String link,
                             String image, String pubDate) {

        boolean checkContain = false; // bi???n ki???m tra items th??m v??o c?? trong file hay kh??ng

        try {

            File file = new File(context.getFilesDir(), fileName);
            if (file.exists()) {

                // ?????c file ????? l???y data trong file
                ArrayList<ChiTietTinTuc> items = new ArrayList<ChiTietTinTuc>();
                FileInputStream fs_in = context.openFileInput(fileName);
                InputStreamReader is = new InputStreamReader(fs_in);
                BufferedReader br = new BufferedReader(is);
                String lineRead = br.readLine();
                String newsNameFile = "", titleFile = "", imageFile = "", linkFile = "", pubDateFile = "";
                while (lineRead != null) {
                    if (!lineRead.equals("#")) {

                        if (lineRead.equals("[newsName]")) {
                            lineRead = br.readLine();
                            newsNameFile = lineRead;

                        } else if (lineRead.equals("[title]")) {
                            lineRead = br.readLine();
                            titleFile = lineRead;

                        } else if (lineRead.equals("[link]")) {
                            lineRead = br.readLine();
                            linkFile = lineRead;

                        } else if (lineRead.equals("[image]")) {
                            lineRead = br.readLine();
                            imageFile = lineRead;

                        } else if (lineRead.equals("[pubDate]")) {
                            lineRead = br.readLine();
                            pubDateFile = lineRead;
                        }

                        if (!newsNameFile.equals("") && !titleFile.equals("") && !linkFile.equals("") && !imageFile.equals("") && !pubDateFile.equals("")) {
                            items.add(new ChiTietTinTuc(newsNameFile, titleFile, linkFile, imageFile, pubDateFile));
                        }

                    } else {
                        lineRead = "";
                        newsNameFile = "";
                        titleFile = "";
                        imageFile = "";
                        linkFile = "";
                        pubDateFile = "";
                    }
                    lineRead = br.readLine();
                }
                br.close();

                // L???y data trong file ???? ?????c l??u trong m???ng r???i ??em so tr??ng
                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).getNewsName().equals(newsName) && items.get(i).getTitle().equals(title) && items.get(i).getLink().equals(link)
                            && items.get(i).getImage().equals(image) && items.get(i).getPubDate().equals(pubDate)) { // tr??ng th?? tho??t v??ng l???p

                        checkContain = true; //tr??ng
                        break;

                    } else {
                        checkContain = false;
                    }
                }

                // checkContain == false t???c l?? items th??m v??o ch??a c?? trong file n??n ghi v??o
                if (checkContain == false) {

                    if (items.size() >= 40) { // ki???m tra m???ng items ?????c t??? file ???? ????? 10 item hay ch??a
                        items.remove(0); // x??a item ?????u

                        items.add(new ChiTietTinTuc(newsName, title, link, image, pubDate)); // l??u  v??o m???ng items

                        file.delete(); // x??a d??? li???u trong file

                        FileOutputStream fs_out = context.openFileOutput(fileName, Context.MODE_APPEND);
                        OutputStreamWriter os = new OutputStreamWriter(fs_out);

                        for (int i = 0; i < items.size(); i++) { // ghi d??? li???u m???i g???m 10 item v??o file
                            os.write("[newsName]" + "\n" + items.get(i).getNewsName() + "\n");
                            os.write("[title]" + "\n" + items.get(i).getTitle() + "\n");
                            os.write("[link]" + "\n" + items.get(i).getLink() + "\n");
                            os.write("[image]" + "\n" + items.get(i).getImage() + "\n");
                            os.write("[pubDate]" + "\n" + items.get(i).getPubDate() + "\n");
                            os.write("#\n");
                        }

                        os.close();

                    } else {
                        FileOutputStream fs_out = context.openFileOutput(fileName, Context.MODE_APPEND);
                        OutputStreamWriter os = new OutputStreamWriter(fs_out);
                        os.write("[newsName]" + "\n" + newsName + "\n");
                        os.write("[title]" + "\n" + title + "\n");
                        os.write("[link]" + "\n" + link + "\n");
                        os.write("[image]" + "\n" + image + "\n");
                        os.write("[pubDate]" + "\n" + pubDate + "\n");
                        os.write("#\n");
                        os.close();

                    }
                }

            } else { // l???n ?????u c??i app th?? s??? ch??a c?? file n??n t???o file v?? l??u d??? li???u items th??m v??o
                FileOutputStream fs_out = context.openFileOutput(fileName, Context.MODE_APPEND);
                OutputStreamWriter os = new OutputStreamWriter(fs_out);
                os.write("[newsName]" + "\n" + newsName + "\n");
                os.write("[title]" + "\n" + title + "\n");
                os.write("[link]" + "\n" + link + "\n");
                os.write("[image]" + "\n" + image + "\n");
                os.write("[pubDate]" + "\n" + pubDate + "\n");
                os.write("#\n");
                os.close();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean checkExistsInFileBookmarNews(Context context, String newsName, String title, String link,
                                                String image, String pubDate) {

        boolean checkContain = false; // bi???n ki???m tra items th??m v??o c?? trong file hay kh??ng

        try {

            File file = new File(context.getFilesDir(), "bookmarkNews.txt");
            if (file.exists()) {

                // ?????c file ????? l???y data trong file
                ArrayList<ChiTietTinTuc> itemsExists = new ArrayList<ChiTietTinTuc>();
                FileInputStream fs_in = context.openFileInput("bookmarkNews.txt");
                InputStreamReader is = new InputStreamReader(fs_in);
                BufferedReader br = new BufferedReader(is);
                String lineRead = br.readLine();
                String newsNameFile = "", titleFile = "", imageFile = "", linkFile = "", pubDateFile = "";
                while (lineRead != null) {
                    if (!lineRead.equals("#")) {

                        if (lineRead.equals("[newsName]")) {
                            lineRead = br.readLine();
                            newsNameFile = lineRead;

                        } else if (lineRead.equals("[title]")) {
                            lineRead = br.readLine();
                            titleFile = lineRead;

                        } else if (lineRead.equals("[link]")) {
                            lineRead = br.readLine();
                            linkFile = lineRead;

                        } else if (lineRead.equals("[image]")) {
                            lineRead = br.readLine();
                            imageFile = lineRead;

                        } else if (lineRead.equals("[pubDate]")) {
                            lineRead = br.readLine();
                            pubDateFile = lineRead;
                        }

                        if (!newsNameFile.equals("") && !titleFile.equals("") && !linkFile.equals("") && !imageFile.equals("") && !pubDateFile.equals("")) {
                            itemsExists.add(new ChiTietTinTuc(newsNameFile, titleFile, linkFile, imageFile, pubDateFile));
                        }

                    } else {
                        lineRead = "";
                        newsNameFile = "";
                        titleFile = "";
                        imageFile = "";
                        linkFile = "";
                        pubDateFile = "";
                    }
                    lineRead = br.readLine();
                }
                br.close();

                // L???y data trong file ???? ?????c l??u trong m???ng r???i ??em so tr??ng
                for (int i = 0; i < itemsExists.size(); i++) {
                    if (itemsExists.get(i).getNewsName().equals(newsName) && itemsExists.get(i).getTitle().equals(title) && itemsExists.get(i).getLink().equals(link)
                            && itemsExists.get(i).getImage().equals(image) && itemsExists.get(i).getPubDate().equals(pubDate)) { // tr??ng th?? tho??t v??ng l???p

                        checkContain = true;
                        break;

                    } else {
                        checkContain = false;
                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return checkContain;
    }

    public void removeExistsInFileBookmarNews(Context context, String newsName,
                                              String title, String link, String image, String pubDate) {

        boolean checkContain = false; // bi???n ki???m tra items th??m v??o c?? trong file hay kh??ng

        try {
            File file = new File(context.getFilesDir(), "bookmarkNews.txt");
            if (file.exists()) {

                // ?????c file ????? l???y data trong file
                ArrayList<ChiTietTinTuc> items = new ArrayList<ChiTietTinTuc>(); // m???ng t???m ch???a d??? li???u trong file
                FileInputStream fs_in = context.openFileInput("bookmarkNews.txt");
                InputStreamReader is = new InputStreamReader(fs_in);
                BufferedReader br = new BufferedReader(is);
                String lineRead = br.readLine();
                String newsNameFile = "", titleFile = "", imageFile = "", linkFile = "", pubDateFile = "";
                while (lineRead != null) {
                    if (!lineRead.equals("#")) {

                        if (lineRead.equals("[newsName]")) {
                            lineRead = br.readLine();
                            newsNameFile = lineRead;

                        } else if (lineRead.equals("[title]")) {
                            lineRead = br.readLine();
                            titleFile = lineRead;

                        } else if (lineRead.equals("[link]")) {
                            lineRead = br.readLine();
                            linkFile = lineRead;

                        } else if (lineRead.equals("[image]")) {
                            lineRead = br.readLine();
                            imageFile = lineRead;

                        } else if (lineRead.equals("[pubDate]")) {
                            lineRead = br.readLine();
                            pubDateFile = lineRead;
                        }

                        if (!newsNameFile.equals("") && !titleFile.equals("") && !linkFile.equals("") && !imageFile.equals("") && !pubDateFile.equals("")) {
                            items.add(new ChiTietTinTuc(newsNameFile, titleFile, linkFile, imageFile, pubDateFile));
                        }

                    } else {
                        lineRead = "";
                        newsNameFile = "";
                        titleFile = "";
                        imageFile = "";
                        linkFile = "";
                        pubDateFile = "";
                    }
                    lineRead = br.readLine();
                }
                br.close();

                // L???y m???ng t???m ???? ?????c l??u trong m???ng r???i ??em so tr??ng v??i items th??m v??o
                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).getNewsName().equals(newsName) && items.get(i).getTitle().equals(title) && items.get(i).getLink().equals(link)
                            && items.get(i).getImage().equals(image) && items.get(i).getPubDate().equals(pubDate)) { // tr??ng th?? tho??t v??ng l???p

                        checkContain = true; //tr??ng
                        items.remove(i);

                        break;

                    } else {
                        checkContain = false;
                    }
                }

                // checkContain == true t???c l?? items th??m v??o c?? trong file n??n ghi v??o
                if (checkContain == true) {

                    file.delete(); // x??a d??? li???u trong file


                    FileOutputStream fs_out = context.openFileOutput("bookmarkNews.txt", Context.MODE_APPEND);
                    OutputStreamWriter os = new OutputStreamWriter(fs_out);

                    for (int i = 0; i < items.size(); i++) {
                        os.write("[newsName]" + "\n" + items.get(i).getNewsName() + "\n");
                        os.write("[title]" + "\n" + items.get(i).getTitle() + "\n");
                        os.write("[link]" + "\n" + items.get(i).getLink() + "\n");
                        os.write("[image]" + "\n" + items.get(i).getImage() + "\n");
                        os.write("[pubDate]" + "\n" + items.get(i).getPubDate() + "\n");
                        os.write("#\n");
                    }
                    os.close();
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
