# compress-over1.26
## 概要
[Apache Commons Compress](https://github.com/apache/commons-compress)のバージョンを1.26.0以上に上げたときに、以下の処理で`@Deprecated`のコンパイル警告が出た:

```java
ZipFile zipFile = new ZipFile(File file);
```

その修正方法を残しておく。


## 修正内容
`ZipFile`のコンストラクタを使わず「`ZipFile.Builder.get()`を使え」とのことなので、それに従って修正する。

```java
ZipFile zipFile = ZipFile.builder().setFile(file).get();
```

### 雑解説
まだ完全に理解していないのでメモ書き程度。

```java
ZipFile
.builder()      // Builderパターンに沿ったインスタンス化処理
.setFile(file)  // 読み取るファイルを指定
.get();         // ZIPアーカイブファイルを読み取るためのクラス
                // （org.apache.commons.compress.archivers.zip.ZipFile）
```

## コード例
### 修正前（< 1.26.0）
```java
// このZipFile(コンストラクタ)がDeprecatedになった
File file = new File(path.toString()); 
try (ZipFile zipFile = new ZipFile(file)) {
    ...
```

### 修正後（>= 1.26.0）
```java
// ZipFile(String path) -> ZipFile.builder().setFile(File file).get()
File file = new File(path.toString());
try (ZipFile zipFile = ZipFile.builder().setFile(file).get()) {
```

実際に動かせる例は以下のリンクを参照：
https://github.com/huraicid/compress-over1.26/blob/main/compress-over126/src/main/java/com/example/Main.java


## 参考
- JavaDoc:
  - [https://commons.apache.org/proper/commons-compress/apidocs/org/apache/commons/compress/archivers/zip/ZipFile.html#<init>(java.io.File)](https://commons.apache.org/proper/commons-compress/apidocs/org/apache/commons/compress/archivers/zip/ZipFile.html#%3Cinit%3E(java.io.File))
- [(commons-compress) branch master updated: Use NIO internally in Lister](https://www.mail-archive.com/commits@commons.apache.org/msg113948.html)
