# compress-over1.26
[Apache Commons Compress](https://github.com/apache/commons-compress)のバージョンを1.26.0以上に上げたときに、以下の処理で`@Deprecated`のコンパイル警告が出た:

```java
ZipFile zipFile = new File(File file);
```

その修正方法を残しておく。
