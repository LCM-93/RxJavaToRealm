## 使用Rxjava2操作Realm

最近项目需要使用[Realm](https://realm.io/)作为本地数据库，Realm作为移动端流行数据库框架之一，自身提供的API已经非常完善且使用简单，但作为使用了RxJava全家桶的项目，直接调用Realm的API总感觉有点不爽，
难道不能使用Rxjava来方便调用Realm吗？

google一下发现自己不是第一个有这种想法的人，已经有人实现了这个想法

> [使用Realm和RxJava打出组合拳的正确姿势](https://www.jianshu.com/p/388beb64d181)

>  [Realm Post](https://academy.realm.io/posts/using-realm-with-rxjava/)

国内的文章也是参考国外某位大神的代码做出了一些修改，但由于文章较早，使用的Rxjava1，与项目使用的Rxjava2稍微有些区别，就花点时间将原代码改造
成了Rxjava2版本，方便以后使用。

源码已上传github：[RxJavaToRealm](https://github.com/lichenming0516/RxJavaToRealm)

简单调用：

```
    /**
     * 增加
     */
    private void add() {
        Random random = new Random();
        int i = random.nextInt(10000);
        final Student student = new Student();
        student.setId(i);
        student.setNanme("name " + i);
        student.setAge(i);
        RealmObservable
                .createObservable(new Function<Realm, Student>() {
                    @Override
                    public Student apply(Realm realm) throws Exception {
                        return realm.copyToRealm(student);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Student>() {
                    @Override
                    public void accept(Student s) throws Exception {
                        Log.d(TAG, "添加成功");
                        Toast.makeText(MainActivity.this, "添加成功！", Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, throwable.getMessage());
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {

                    }
                });
    }

```

```
    /**
     * 查找
     */
    private void find() {
        RealmObservable
                .createObservable(new Function<Realm, List<Student>>() {
                    @Override
                    public List<Student> apply(Realm realm) throws Exception {

                        RealmResults<Student> all = realm.where(Student.class).findAll();
                        return realm.copyFromRealm(all);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Student>>() {
                    @Override
                    public void accept(List<Student> students) throws Exception {
                        tv.setText(students.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }
```