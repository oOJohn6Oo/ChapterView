# ChapterView
带标题、内容、bulletspan的内容view。

项目中往往有一些看起来很简单，做起来头大的样式，用`TextView`组合起来感觉特别蠢。所以有了这次定制。

-------

``` xml
<com.john6.chapterview.ChapterView
    android:id="@+id/chapter_view"
    android:background="@android:color/white"
    android:padding="10dp"
    app:want_chapter_title="true"
    app:want_chapter_content="true"
    app:want_chapter_bullet="true"
    app:want_chapter_line="true"
    app:chapter_title_text="@string/chapter_title"
    app:chapter_content_text="@array/chapter_content"
    app:chapter_content_single_line="false"
    app:chapter_content_line_spacing="10dp"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintLeft_toLeftOf="parent"
    app:layout_constraintRight_toRightOf="parent"
    app:layout_constraintTop_toTopOf="parent" />
```
![效果](https://upload-images.jianshu.io/upload_images/14730476-c3aa3edf970b3ee7.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/300)


## 如何引入
```
implementation 'com.john6.uikit:chapterview:0.1'
```
## 项目地址
[github](https://github.com/oOjohn6Oo/ChapterView)

## 实现思路
待更新...