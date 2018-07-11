package cn.shicancan.camserial.presenter;


interface IPushVideoAidlInterface {

            /**
              *  开始推流
              *  @param url RTMP 地址
              *  @return 是否成功
              */
           boolean start_push(String url);
           /**
             *  停止推流
             */
           void stop_push();
           /**
             * 是否正在推流
             * @return 推流状态
             */
           boolean is_pushing();
}
