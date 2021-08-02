const mealAjaxUrl = "ui/meals/";

// https://stackoverflow.com/a/5064235/548473
var ctx = {
    ajaxUrl: mealAjaxUrl,
    updateTable: function () {
        $.ajax({
            type: "GET",
            url: mealAjaxUrl + "filter",
            data: $("#filter").serialize()
        }).done(updateTableByData);
    }
};

function clearFilter() {
    $("#filter")[0].reset();
    $.get(mealAjaxUrl, updateTableByData);
}

$(function () {
    makeEditable(
        $("#datatable").DataTable({
            "paging": false,
            "info": true,
            "columns": [
                {
                    "data": "dateTime"
                },
                {
                    "data": "description"
                },
                {
                    "data": "calories"
                },
                {
                    "defaultContent": "Edit",
                    "orderable": false
                },
                {
                    "defaultContent": "Delete",
                    "orderable": false
                }
            ],
            "order": [
                [
                    0,
                    "desc"
                ]
            ]
        })
    );

    // $.datetimepicker.setLocale(localeCode);

    $('#dateTime').datetimepicker({
        format: 'Y-m-d H:i'
    });

    $('#startDate').datetimepicker({
        timepicker: false,
        format: 'Y-m-d',
        // formatDate: 'Y-m-d'
    });

    $('#endDate').datetimepicker({
        timepicker: false,
        format: 'Y-m-d',
        // formatDate: 'Y-m-d'
    });

    $('#startTime').datetimepicker({
        datepicker: false,
        format: 'H:i'
    });

    $('#endTime').datetimepicker({
        datepicker: false,
        format: 'H:i'
    });
});