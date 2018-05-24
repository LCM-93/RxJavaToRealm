package com.lcm.rxjavatorealm.RxRealm;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.functions.Action;
import io.realm.Realm;
import io.realm.exceptions.RealmException;

/**
 * ****************************************************************
 * Author: LCM
 * Date: 2018/5/24 13:54
 * Desc:
 * *****************************************************************
 */
public abstract class ObservableOnSubscribeRealm<T> implements ObservableOnSubscribe<T> {

    private final List<ObservableEmitter<? super T>> observableEmitters = new ArrayList<>();
    private final AtomicBoolean canceled = new AtomicBoolean();
    private final Object lock = new Object();

    @Override
    public void subscribe(ObservableEmitter<T> emitter) throws Exception {
        synchronized (lock) {
            boolean canceled = this.canceled.get();
            if (!canceled && !observableEmitters.isEmpty()) {
                emitter.setDisposable(newUnDisposableAction(emitter));
                observableEmitters.add(emitter);
                return;
            } else if (canceled) {
                return;
            }
        }

        emitter.setDisposable(newUnDisposableAction(emitter));
        observableEmitters.add(emitter);

        Realm realm = Realm.getDefaultInstance();
        boolean withError = false;

        T object = null;
        try {
            if (!this.canceled.get()) {
                realm.beginTransaction();
                object = get(realm);
                if (!this.canceled.get()) {
                    realm.commitTransaction();
                } else {
                    realm.cancelTransaction();
                }
            }
        } catch (RuntimeException e) {
            realm.cancelTransaction();
            sendOnError(new RealmException("Error during transaction.", e));
            withError = true;
        } catch (Error e) {
            realm.cancelTransaction();
            sendOnError(e);
            withError = true;
        }

        if (!this.canceled.get() && !withError) {
            sendOnNext(object);
        }

        try {
            realm.close();
        } catch (RealmException e) {
            sendOnError(e);
            withError = true;
        }
        if (!withError) {
            sendOnComplete();
        }
        this.canceled.set(false);

    }

    private void sendOnNext(T object) {
        for (int i = 0; i < observableEmitters.size(); i++) {
            ObservableEmitter<? super T> emitter = observableEmitters.get(i);
            if (object == null) {
                emitter.onNext((T) new Object());
            } else {
                emitter.onNext(object);
            }
        }
    }

    private void sendOnError(Throwable e) {
        for (int i = 0; i < observableEmitters.size(); i++) {
            ObservableEmitter<? super T> emitter = observableEmitters.get(i);
            emitter.onError(e);
        }
    }

    private void sendOnComplete() {
        for (int i = 0; i < observableEmitters.size(); i++) {
            ObservableEmitter<? super T> emitter = observableEmitters.get(i);
            emitter.onComplete();
        }
    }


    private Disposable newUnDisposableAction(final ObservableEmitter<? super T> observableEmitter) {
        return Disposables.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                synchronized (lock) {
                    observableEmitters.remove(observableEmitter);
                    if (observableEmitters.isEmpty()) {
                        canceled.set(true);
                    }
                }
            }
        });
    }


    public abstract T get(Realm realm) throws Exception;
}
