struct manager;              // Declaration
struct employee {
  struct manager* boss;
  struct employee* next_employee;
  ...
};

struct manager {             // Definition
  struct employee* first_employee;
  ...
};
