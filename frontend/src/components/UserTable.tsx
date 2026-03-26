import React, { useMemo, useState } from "react";
import { Input, Pagination, Space, Table, Tag, Typography } from "antd";
import type { ColumnsType } from "antd/es/table";

const { Title } = Typography;

export type UserRole = "管理员" | "编辑" | "访客";
export type UserStatus = "启用" | "禁用";

export interface UserItem {
  id: string;
  name: string;
  email: string;
  role: UserRole;
  status: UserStatus;
}

export interface UserTableProps {
  users: UserItem[];
}

const PAGE_SIZE = 5;

export const UserTable: React.FC<UserTableProps> = ({ users }) => {
  const [searchName, setSearchName] = useState("");
  const [currentPage, setCurrentPage] = useState(1);

  // 根据姓名关键字做前端过滤，提升输入时的响应性
  const filteredUsers = useMemo(() => {
    const keyword = searchName.trim().toLowerCase();
    if (!keyword) {
      return users;
    }

    return users.filter((user) => user.name.toLowerCase().includes(keyword));
  }, [users, searchName]);

  // 先过滤再分页，保证分页总数与展示结果一致
  const paginatedUsers = useMemo(() => {
    const start = (currentPage - 1) * PAGE_SIZE;
    return filteredUsers.slice(start, start + PAGE_SIZE);
  }, [filteredUsers, currentPage]);

  const columns: ColumnsType<UserItem> = [
    {
      title: "姓名",
      dataIndex: "name",
      key: "name",
    },
    {
      title: "邮箱",
      dataIndex: "email",
      key: "email",
    },
    {
      title: "角色",
      dataIndex: "role",
      key: "role",
      render: (role: UserRole) => <Tag color="blue">{role}</Tag>,
    },
    {
      title: "状态",
      dataIndex: "status",
      key: "status",
      render: (status: UserStatus) => (
        <Tag color={status === "启用" ? "green" : "default"}>{status}</Tag>
      ),
    },
  ];

  return (
    <Space direction="vertical" size={16} style={{ width: "100%" }}>
      <Title level={4}>用户管理</Title>
      <Input
        allowClear
        placeholder="请输入姓名搜索"
        value={searchName}
        onChange={(event) => {
          // 搜索条件变化时重置到第一页，避免出现空页
          setSearchName(event.target.value);
          setCurrentPage(1);
        }}
      />
      <Table<UserItem>
        rowKey="id"
        columns={columns}
        dataSource={paginatedUsers}
        pagination={false}
      />
      <Pagination
        current={currentPage}
        pageSize={PAGE_SIZE}
        total={filteredUsers.length}
        onChange={setCurrentPage}
        showSizeChanger={false}
      />
    </Space>
  );
};

export default UserTable;
