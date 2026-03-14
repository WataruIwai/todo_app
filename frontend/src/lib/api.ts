export type Todo = {
  id: number;
  title: string;
  userId: number;
  done: boolean;
  recurring: boolean;
};

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:7070";

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, init);

  if (!response.ok) {
    const text = await response.text();
    throw new Error(text || `Request failed: ${response.status}`);
  }

  const contentType = response.headers.get("content-type");
  if (contentType?.includes("application/json")) {
    return (await response.json()) as T;
  }

  return undefined as T;
}

function toFormBody(values: Record<string, string | number | boolean>) {
  const body = new URLSearchParams();

  Object.entries(values).forEach(([key, value]) => {
    body.set(key, String(value));
  });

  return body;
}

export async function fetchTodos(): Promise<Todo[]> {
  return request<Todo[]>("/todos");
}

export async function createTodo(input: { title: string; userId: number }) {
  await request<void>("/todos", {
    method: "POST",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
    },
    body: toFormBody(input),
  });
}

export async function updateTodo(input: {
  id: number;
  title: string;
  userId: number;
  done: boolean;
}) {
  return request<Todo>(`/todos/${input.id}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
    },
    body: toFormBody({
      title: input.title,
      userId: input.userId,
      done: input.done,
    }),
  });
}

export async function deleteTodo(id: number, userId: number) {
  await request<void>(`/todos/${id}?userId=${userId}`, {
    method: "DELETE",
  });
}

export async function registerRecurring(id: number, userId: number) {
  return request<Todo>(`/todos/${id}/recurring?userId=${userId}`, {
    method: "POST",
  });
}

export async function unregisterRecurring(id: number, userId: number) {
  return request<Todo>(`/todos/${id}/recurring?userId=${userId}`, {
    method: "DELETE",
  });
}
