

 // save
                    SQLiteDatabase db = LitePal.getDatabase();
                    Album album = new Album();
                    album.setName("album");
                    album.setPrice(10.99f);
                    album.save();
                    Song song1 = new Song();
                    song1.setName("song1");
                    song1.setDuration(320);
                    song1.setAlbum(album);
                    song1.save();
                    Song song2 = new Song();
                    song2.setName("song2");
                    song2.setDuration(356);
                    song2.setAlbum(album);
                    song2.save();

                    // update
                    Album albumToUpdate = LitePal.find(Album.class, 1);
                    albumToUpdate.setPrice(20.99f); // raise the price
                    albumToUpdate.save();


                    albumToUpdate = new Album();
                    albumToUpdate.setPrice(20.99f); // raise the price
                    albumToUpdate.update(1);

                    albumToUpdate = new Album();
                    albumToUpdate.setPrice(20.99f); // raise the price
                    albumToUpdate.updateAll("name = ?", "album");

                    // delete
                    LitePal.delete(Song.class, 1);
                    LitePal.deleteAll(Song.class, "duration > ?", "350");


                    //Query data
                    Song song = LitePal.find(Song.class, 1);
                    List<Song> allSongs = LitePal.findAll(Song.class);
                    List<Song> songs =
                            LitePal.where("name like ? and duration < ?", "song%", "200").order(
                                    "duration").find(Song.class);


                    //Multiple databases
                    //This will create a demo2 database with singer, album and song tables.
                    LitePalDB litePalDB = new LitePalDB("demo2", 1);
                    litePalDB.addClassName(Singer.class.getName());
                    litePalDB.addClassName(Album.class.getName());
                    litePalDB.addClassName(Song.class.getName());
                    LitePal.use(litePalDB);

                    litePalDB = LitePalDB.fromDefault("newdb");
                    LitePal.use(litePalDB);


                    //LitePal.useDefault(); // 回到默认数据库
                    //LitePal.deleteDatabase("newdb"); // 删除数据库


// 启动activity的常用方式
ComponentName cn = new ComponentName("当前Activity的全类名","启动Activity的全类名") ;
Intent intent = new Intent() ;
intent.setComponent(cn) ;
startActivity(intent) ;

Intent intent = new Intent("android.intent.action.MAIN");
intent.setClassName("当前Act的全限定类名","启动Act的全限定类名");
startActivity(intent);

Intent intent = new Intent();
intent.setAction("my_action");
intent.addCategory("my_category");
startActivity(intent);


// 申请权限方法
 PermissionX.init(activity)
                                .permissions(Manifest.permission.READ_CONTACTS, Manifest.permission.CAMERA, Manifest.permission.CALL_PHONE)
                                .request(new RequestCallback() {
                                    @Override
                                    public void onResult(boolean allGranted, List<String> grantedList, List<String> deniedList) {

                                    }
                                });


