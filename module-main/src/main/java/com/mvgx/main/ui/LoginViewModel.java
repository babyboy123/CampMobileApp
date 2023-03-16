package com.mvgx.main.ui;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.mvgx.common.BuildConfig;
import com.mvgx.common.http.Repository;
import com.mvgx.common.base.BaseViewModel;
import com.mvgx.common.http.request.RequestLoginInfo;
import com.mvgx.common.http.response.ResponseLoginInfo;
import com.mvgx.common.init.binding.command.BindingAction;
import com.mvgx.common.init.binding.command.BindingCommand;
import com.mvgx.common.init.bus.event.SingleLiveEvent;
import com.mvgx.common.init.http.BaseResponse;
import com.mvgx.common.init.utils.RxUtils;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;

/**
 * @Author Arthur
 * @Date 2023/03/16 13:42
 */
public class LoginViewModel extends BaseViewModel<Repository> {

    //用户名的绑定
    public ObservableField<String> userName = new ObservableField<>("");
    //密码的绑定
    public ObservableField<String> password = new ObservableField<>("");

    public ObservableField<String> code = new ObservableField<>("");

    public ObservableField<Boolean> onIsClickLogin = new ObservableField<Boolean>();
    //封装一个界面发生改变的观察者
    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        //密码开关观察者
        public SingleLiveEvent<Boolean> pSwitchEvent = new SingleLiveEvent<>();
    }

    public LoginViewModel(@NonNull Application application, Repository repository) {
        super(application, repository);
        //从本地取得数据绑定到View层
        if (BuildConfig.DEBUG) {
            userName.set("bookingCoo");
            password.set("password");
            code.set("123456");
        }
    }

    public void setPasswordIsVisible() {
        //让观察者的数据改变,逻辑从ViewModel层转到View层，在View层的监听则会被调用
        uc.pSwitchEvent.setValue(uc.pSwitchEvent.getValue() == null || !uc.pSwitchEvent.getValue());
    }

    //登录按钮的点击事件
    public BindingCommand loginOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            login();
        }
    });

    /**
     * 网络模拟一个登陆操作
     **/
    @SuppressWarnings("unchecked")
    private void login() {
        try {
            RequestLoginInfo requestLoginInfo = new RequestLoginInfo();
            requestLoginInfo.setUsername(userName.get().trim());
//            String mPasswordStr = RSADigitalSignUtil.encryptByPublicKey(password.get().trim(), AppConfig.pubKey);
            requestLoginInfo.setPassword("OyyMLQKnBR+THyF9dGvH/wBOnUd0HwDAvl2x+7BKXhp09CqRl+q+8I3ng8xMItrO7OK3lIYEs23JjLQRvTiGMzhMaN1DfyiLwE6XUugfq0AWW1YZd1ZZhXlfVfwmKNyGA6D83IzSaaF2gX72I5KbAJSVsRZrHPGdaHHXEt6F9qI=");
            model.login(requestLoginInfo)
                    .compose(RxUtils.schedulersTransformer())
                    .compose(RxUtils.exceptionTransformer())
                    .doOnSubscribe(this)
                    .doOnSubscribe(new Consumer<Disposable>() {
                        @Override
                        public void accept(Disposable disposable) throws Exception {
                            showDialog();
                        }
                    }).subscribe(new DisposableObserver<BaseResponse<ResponseLoginInfo>>() {
                        @Override
                        public void onNext(@NonNull BaseResponse<ResponseLoginInfo> result) {
                            if (null != result && result.isOk()) {
                                startActivity(MainActivity.class);
                            } else {
                                dismissDialog();
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable throwable) {
                            dismissDialog();
                        }

                        @Override
                        public void onComplete() {
                        }
                    });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}