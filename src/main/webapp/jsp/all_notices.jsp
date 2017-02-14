<%@ page isELIgnored="false" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%request.setCharacterEncoding("UTF-8");%>
<c:set var="path" value="${pageContext.request.contextPath}"/>
<html>
<head>
    <title>Все заявки</title>
    <meta charset="utf-8">
    <script type="text/javascript" src="<c:url value ="/js/jquery-3.1.1.min.js"/>"></script>
    <script type="text/javascript" src="<c:url value ="/js/datatables.min.js"/>"></script>
    <link href="<c:url value="/css/dark/table.css" />" rel="stylesheet">
</head>
<body>
<jsp:include page="menu.jsp"/>
<p style="text-align:center">Список всех заявок из БД</p>
<table id="notice_table">
    <thead>
        <tr>
            <th></th>
            <th>ID</th>
            <th>Пользователь</th>
            <th>Должность</th>
            <th>Отдел</th>
            <th>Ресурсы</th>
            <th>Время подачи</th>
            <th>Статус</th>
            <th></th>
        </tr>
    </thead>
    <tbody>
        <c:if test="${not empty reportingNotice}">
            <c:forEach var="notice" items="${reportingNotice}">
                <tr>
                    <td><a class="view-btn" href="notice/${notice.id}">Подробнее</a></td>
                    <td>${notice.id}</td>
                    <td>${notice.employers_id.fio}</td>
                    <td>${notice.employers_id.post_id.name}</td>
                    <td>${notice.employers_id.sector_id.division_id.name}</td>
                    <td>Русурсы</td>
                    <td>${notice.dateSet}</td>
                    <c:if test="${notice.status == true}">
                        <td>
                            <span class="notice-status-true">&#10004;</span>
                        </td>
                    </c:if>
                    <c:if test="${notice.status == false}">
                        <td>
                            <span class="notice-status-false">&#10006;</span>
                        </td>
                    </c:if>
                    <td><a class="delete-btn" href="${path}/delete/${notice.id}">Удалить</a></td>
                </tr>
            </c:forEach>
        </c:if>
    </tbody>
</table>

<script>
    $(function () {
        $("#notice_table").dataTable({
            language: {
                "processing": "Подождите",
                "search": "Поиск:",
                "lengthMenu": "Показать _MENU_ записей",
                "info": "Записи с _START_ до _END_ из _TOTAL_ записей",
                "infoEmpty": "Записи с 0 до 0 из 0 записей",
                "infoFiltered": "(отфильтровано из _MAX_ записей)",
                "infoPostFix": "",
                "loadingRecords": "Загрузка записей",
                "zeroRecords": "Записи отсутствуют",
                "emptyTable": "В таблице отсутствуют данные",
                "paginate": {
                    "first": "Первая",
                    "previous": "Предыдущая",
                    "next": "Следующая",
                    "last": "Последняя"
                },
                "aria": {
                    "sortAscending": ": активировать для сортировки столбца по возрастанию",
                    "sortDescending": ": активировать для сортировки столбца по убыванию"
                }
            },
            stateSave: true,
        });
    })
</script>
</body>
</html>