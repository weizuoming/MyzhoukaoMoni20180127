package com.example.hello.myzhoukaomoni20180127.activity;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 *  on 2018/01/27
 *
 * 公共参数封装分为两种方式....source=android,,,appVersion=100
 *      一:封装到doGet和doPost方法中
 *
 *
 *      二:封装到拦截器(interceptor):拦截一个请求,然后添加参数进去
 */
public class OkHttp3Util_03 {


    /**
     * 懒汉 安全 加同步
     * 1.私有的静态成员变量 只声明不创建
     * 2.私有的构造方法
     * 3.提供返回实例的静态方法
     */
    private static OkHttpClient okHttpClient = null;


    private OkHttp3Util_03() {
    }

    public static OkHttpClient getInstance() {

        if (okHttpClient == null) {
            //加同步安全
            synchronized (OkHttp3Util_03.class) {
                if (okHttpClient == null) {
                    //okhttp可以缓存数据....指定缓存路径
                    File sdcache = new File(Environment.getExternalStorageDirectory(), "cache");
                    //指定缓存大小
                    int cacheSize = 10 * 1024 * 1024;

                    okHttpClient = new OkHttpClient.Builder()//构建器
                            .connectTimeout(15, TimeUnit.SECONDS)//连接超时
                            .writeTimeout(20, TimeUnit.SECONDS)//写入超时
                            .readTimeout(20, TimeUnit.SECONDS)//读取超时

                            .addInterceptor(new CommonParamsInterceptor())//添加应用拦截器的方法

                            .cache(new Cache(sdcache.getAbsoluteFile(), cacheSize))//设置缓存
                            .build();
                }
            }

        }

        return okHttpClient;
    }

    /**
     * get请求
     * 参数1 url
     * 参数2 回调Callback
     *
     * oldUrl表示老的地址,,,是没封装公共参数之前的地址....公共参数指的是source=android
     *
     */

    public static void doGet(String oldUrl, Callback callback) {


        //创建OkHttpClient请求对象
        OkHttpClient okHttpClient = getInstance();
        //创建Request
        Request request = new Request.Builder().url(oldUrl).build();
        //得到Call对象
        Call call = okHttpClient.newCall(request);
        //执行异步请求
        call.enqueue(callback);//异步执行..子线程不会阻塞...excute同步执行...阻塞线程


    }

    /**
     * post请求
     * 参数1 url
     * 参数2 Map<String, String> params post请求的时候给服务器传的数据
     *      add..("","")
     *      add()
     */

    public static void doPost(String url, Map<String, String> params, Callback callback) {

        //创建OkHttpClient请求对象
        OkHttpClient okHttpClient = getInstance();
        //3.x版本post请求换成FormBody 封装键值对参数

        FormBody.Builder builder = new FormBody.Builder();
        //遍历集合,,,map集合遍历方式
        for (String key : params.keySet()) {
            builder.add(key, params.get(key));

        }


        //创建Request....formBody...new formBody.Builder()...add()....build()
        Request request = new Request.Builder().url(url).post(builder.build()).build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);

    }
    /**
     * 封装公共参数的拦截器
     */
    private static class CommonParamsInterceptor implements Interceptor{

        //intercept方法就是拦截的意思....拦截的是一个请求,,,一旦拦截之后可以对请求的参数进行处理
        //Chain chain 链条的意思
        @Override
        public Response intercept(Chain chain) throws IOException {

            Log.e("----","拦截器------");

            //调用request()方法得到拦截的请求
            Request request = chain.request();

            //获取请求的方式
            String method = request.method();//GET POST
            //拦截的请求的路径
            String oldUrl = request.url().toString();

            //要添加的公共参数...map
            Map<String,String> map = new HashMap<>();
            map.put("source","android");

            if ("GET".equals(method)){
                StringBuilder stringBuilder = new StringBuilder();//创建一个stringBuilder...字符串缓冲区

                stringBuilder.append(oldUrl);

                if (oldUrl.contains("?")){
                    //?在最后面....2类型
                    if (oldUrl.indexOf("?") == oldUrl.length()-1){

                    }else {
                        //3类型...拼接上&
                        stringBuilder.append("&");
                    }
                }else {
                    //不包含? 属于1类型,,,先拼接上?号
                    stringBuilder.append("?");
                }

                //添加公共参数....source=android&appVersion=100&
                for (Map.Entry<String,String> entry: map.entrySet()) {
                    //拼接
                    stringBuilder.append(entry.getKey())
                            .append("=")
                            .append(entry.getValue())
                            .append("&");
                }

                //删掉最后一个&符号
                if (stringBuilder.indexOf("&") != -1){
                    stringBuilder.deleteCharAt(stringBuilder.lastIndexOf("&"));
                }
                //得到含有公共参数的新路径.....使用新路径去请求
                String newUrl = stringBuilder.toString();

                //新的路径构建一个新的请求
                request = request.newBuilder().url(newUrl).build();

            }else if ("POST".equals(method)){
                Log.e("--oldUrl--",oldUrl);
                //参数在请求的实体内容上....拿到以前的参数,把以前的参数和公共参数全都添加到请求实体上
                RequestBody requestBody = request.body();
                if (requestBody instanceof FormBody){//前边是不是后边的子类/实例对象
                    FormBody oldBody = (FormBody) requestBody;//keywords...page

                    FormBody.Builder builder = new FormBody.Builder();

                    //先添加之前的参数
                    for (int i = 0;i<oldBody.size();i++){
                        //键值对的形式添加
                        builder.add(oldBody.name(i),oldBody.value(i));
                    }
                    //添加公共参数
                    for (Map.Entry<String,String> entry: map.entrySet()) {

                        builder.add(entry.getKey(),entry.getValue());

                    }

                    //构建一个新的请求
                    request = request.newBuilder().url(oldUrl).post(builder.build()).build();


                }

            }


            //执行请求 获取到响应
            Response response = chain.proceed(request);
            return response;
        }
    }

}
