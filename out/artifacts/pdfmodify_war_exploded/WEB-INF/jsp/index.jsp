<%--
  Created by IntelliJ IDEA.
  User: MeoAdmin
  Date: 2020/12/25
  Time: 10:23
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isELIgnored="false" %>
<html>
<head>
    <title>upLoad</title>
    <link href="https://cdn.staticfile.org/twitter-bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
${msg}
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

        <button type="submit" class="btn btn-default">上传并且处理</button>
    </form>

</div>
</body>
</html>
