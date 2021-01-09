<%--
  Created by IntelliJ IDEA.
  User: BavenCat
  Date: 2020/6/25
  Time: 14:59
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>createPerson</title>
    <link href="https://cdn.staticfile.org/twitter-bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container">
    <div class="row clearfix">
        <div class="col-md-12 column">
            <div class="page-header">
                <h1>
                    <small>Upload</small>
                </h1>
            </div>
        </div>
    </div>

    <form action="/upload" enctype="multipart/form-data" method="post">

        <div class="form-group">
            <label>文件</label>
            <input type="file" class="form-control" name="file">
        </div>

        <button type="submit" class="btn btn-default">上传并处理</button>

        <a href="/deal" class="btn btn-default">处理</a>
    </form>

</div>
</body>
</html>
