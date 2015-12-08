function page(pageNo) {
    window.location = "agents.do?method=getAgents&pageNo=" + pageNo;
}

function prePage(agentCode, name, pageNo) {
    if (pageNo == 1) {
        alert("已是第一页");
        return;
    }
    pageNo--;
    window.location = "agents.do?method=getAgents&pageNo=" + pageNo + "&agentCode=" + agentCode + "&name=" + name;
}

function nextPage(agentCode, name, pageNo, pageCount) {
    if (pageNo == pageCount) {
        alert("已是最后一页");
        return;
    }
    pageNo++;

    window.location = "agents.do?method=getAgents&pageNo=" + pageNo + "&agentCode=" + agentCode + "&name=" + name;


}

//


function prePage1(pageNo) {
    if (pageNo == 1) {
        alert("已是第一页");
        return;
    }
    pageNo--;
    window.location = "agents.do?method=detailAgents&pageNo=" + pageNo;
}

function nextPage1(pageNo, pageCount) {
    if (pageNo == pageCount) {
        alert("已是最后一页");
        return;
    }
    pageNo++;

    window.location = "agents.do?method=detailAgents&pageNo=" + pageNo+"&agentId=${agents.id}&name=${agents.name}&address=${agents.address}&mobile=${agents.mobile}&respArea=${agents.respArea}&personInCharge=${agents.personInCharge}&monthTarget=${agents.monthTarget}&yearTarget=${agents.yearTarget}&agentCode=${agents.agentCode}&state=${agents.state}&year=${agents.year}";


}