import { useEffect, useState } from "react";
import {
  CheckCircle2,
  Circle,
  LoaderCircle,
  Plus,
  RefreshCw,
  Repeat2,
  Trash2,
  UserRound,
} from "lucide-react";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import {
  createTodo,
  deleteTodo,
  fetchTodos,
  registerRecurring,
  type Todo,
  unregisterRecurring,
  updateTodo,
} from "@/lib/api";

function App() {
  const [todos, setTodos] = useState<Todo[]>([]);
  const [userIdInput, setUserIdInput] = useState("1");
  const [newTitle, setNewTitle] = useState("");
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const userId = Number.parseInt(userIdInput, 10);
  const selectedUserTodos = todos;
  const completedCount = selectedUserTodos.filter((todo) => todo.done).length;
  const recurringCount = selectedUserTodos.filter((todo) => todo.recurring).length;

  async function loadTodos() {
    if (Number.isNaN(userId)) {
      setTodos([]);
      setError("User ID must be a number");
      setLoading(false);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const data = await fetchTodos(userId);
      setTodos(data);
    } catch (loadError) {
      setError(loadError instanceof Error ? loadError.message : "Failed to load todos");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void loadTodos();
  }, []);

  async function handleCreateTodo() {
    if (!newTitle.trim() || Number.isNaN(userId)) {
      return;
    }

    setSubmitting(true);
    setError(null);

    try {
      await createTodo({ title: newTitle.trim(), userId });
      setNewTitle("");
      await loadTodos();
    } catch (createError) {
      setError(createError instanceof Error ? createError.message : "Failed to create todo");
    } finally {
      setSubmitting(false);
    }
  }

  async function handleToggleDone(todo: Todo) {
    setSubmitting(true);
    setError(null);

    try {
      const updatedTodo = await updateTodo({
        id: todo.id,
        title: todo.title,
        userId: todo.userId,
        done: !todo.done,
      });
      setTodos((currentTodos) =>
        currentTodos.map((currentTodo) =>
          currentTodo.id === updatedTodo.id ? updatedTodo : currentTodo,
        ),
      );
    } catch (updateError) {
      setError(updateError instanceof Error ? updateError.message : "Failed to update todo");
    } finally {
      setSubmitting(false);
    }
  }

  async function handleToggleRecurring(todo: Todo) {
    setSubmitting(true);
    setError(null);

    try {
      const updatedTodo = todo.recurring
        ? await unregisterRecurring(todo.id, todo.userId)
        : await registerRecurring(todo.id, todo.userId);

      setTodos((currentTodos) =>
        currentTodos.map((currentTodo) =>
          currentTodo.id === updatedTodo.id ? updatedTodo : currentTodo,
        ),
      );
    } catch (toggleError) {
      setError(
        toggleError instanceof Error ? toggleError.message : "Failed to update recurring status",
      );
    } finally {
      setSubmitting(false);
    }
  }

  async function handleDeleteTodo(todo: Todo) {
    setSubmitting(true);
    setError(null);

    try {
      await deleteTodo(todo.id, todo.userId);
      setTodos((currentTodos) =>
        currentTodos.filter((currentTodo) => currentTodo.id !== todo.id),
      );
    } catch (deleteError) {
      setError(deleteError instanceof Error ? deleteError.message : "Failed to delete todo");
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <main className="min-h-screen px-4 py-8 sm:px-6 lg:px-8">
      <div className="mx-auto flex w-full max-w-6xl flex-col gap-6">
        <section className="grid gap-6 lg:grid-cols-[1.15fr_0.85fr]">
          <Card className="overflow-hidden border-primary/15 bg-gradient-to-br from-white via-white to-secondary/70">
            <CardHeader className="gap-4">
              <Badge className="w-fit" variant="accent">
                Daily task dashboard
              </Badge>
              <CardTitle className="max-w-2xl text-4xl leading-tight sm:text-5xl">
                Web first, backed by your Java API, ready for recurring task flows.
              </CardTitle>
              <CardDescription className="max-w-xl text-base">
                This frontend talks directly to the existing Todo backend. You can create
                todos, mark them done, and toggle recurring registration against the
                endpoints we already built.
              </CardDescription>
            </CardHeader>
            <CardContent className="grid gap-4 sm:grid-cols-3">
              <div className="rounded-3xl bg-background/80 p-4">
                <p className="text-sm text-muted-foreground">Visible todos</p>
                <p className="mt-2 text-3xl font-semibold">{selectedUserTodos.length}</p>
              </div>
              <div className="rounded-3xl bg-background/80 p-4">
                <p className="text-sm text-muted-foreground">Completed</p>
                <p className="mt-2 text-3xl font-semibold">{completedCount}</p>
              </div>
              <div className="rounded-3xl bg-background/80 p-4">
                <p className="text-sm text-muted-foreground">Recurring</p>
                <p className="mt-2 text-3xl font-semibold">{recurringCount}</p>
              </div>
            </CardContent>
          </Card>

          <Card className="border-accent/20">
            <CardHeader>
              <CardTitle className="flex items-center gap-2 text-2xl">
                <UserRound className="h-5 w-5 text-primary" />
                Working user
              </CardTitle>
              <CardDescription>
                The backend currently uses `userId` on each request, so we keep that visible in
                the UI for now.
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <label className="block space-y-2">
                <span className="text-sm font-medium">User ID</span>
                <Input
                  inputMode="numeric"
                  value={userIdInput}
                  onChange={(event) => setUserIdInput(event.target.value)}
                  placeholder="1"
                />
              </label>
              <Button
                className="w-full"
                variant="secondary"
                onClick={() => void loadTodos()}
                disabled={loading}
              >
                <RefreshCw className="mr-2 h-4 w-4" />
                Refresh from API
              </Button>
              {error ? (
                <div className="rounded-2xl border border-destructive/20 bg-destructive/10 px-4 py-3 text-sm text-destructive">
                  {error}
                </div>
              ) : null}
            </CardContent>
          </Card>
        </section>

        <section className="grid gap-6 lg:grid-cols-[0.8fr_1.2fr]">
          <Card>
            <CardHeader>
              <CardTitle className="text-2xl">Create a todo</CardTitle>
              <CardDescription>
                This uses the current `POST /todos` endpoint and refreshes the list after the
                backend accepts the new record.
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <label className="block space-y-2">
                <span className="text-sm font-medium">Title</span>
                <Input
                  value={newTitle}
                  onChange={(event) => setNewTitle(event.target.value)}
                  placeholder="Plan the next daily win"
                />
              </label>
              <Button
                className="w-full"
                onClick={() => void handleCreateTodo()}
                disabled={submitting || !newTitle.trim() || Number.isNaN(userId)}
              >
                <Plus className="mr-2 h-4 w-4" />
                Add todo
              </Button>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0">
              <div className="space-y-1.5">
                <CardTitle className="text-2xl">Todos</CardTitle>
                <CardDescription>
                  Done toggles call `PUT /todos/:id`. Recurring toggles call the dedicated
                  recurring endpoints.
                </CardDescription>
              </div>
              {loading ? <LoaderCircle className="h-5 w-5 animate-spin text-primary" /> : null}
            </CardHeader>
            <CardContent>
              <div className="space-y-3">
                {selectedUserTodos.length === 0 && !loading ? (
                  <div className="rounded-[1.25rem] border border-dashed border-border bg-background/70 px-4 py-10 text-center text-sm text-muted-foreground">
                    No todos found for this user yet.
                  </div>
                ) : null}

                {selectedUserTodos.map((todo) => (
                  <article
                    key={todo.id}
                    className="flex flex-col gap-4 rounded-[1.25rem] border border-border/70 bg-background/80 p-4 sm:flex-row sm:items-center sm:justify-between"
                  >
                    <div className="flex items-start gap-3">
                      <button
                        type="button"
                        className="mt-0.5 text-primary"
                        onClick={() => void handleToggleDone(todo)}
                        disabled={submitting}
                        aria-label={todo.done ? "Mark as not done" : "Mark as done"}
                      >
                        {todo.done ? (
                          <CheckCircle2 className="h-6 w-6" />
                        ) : (
                          <Circle className="h-6 w-6" />
                        )}
                      </button>

                      <div className="space-y-2">
                        <div className="flex flex-wrap items-center gap-2">
                          <h2
                            className={`text-lg font-semibold ${
                              todo.done ? "text-muted-foreground line-through" : ""
                            }`}
                          >
                            {todo.title}
                          </h2>
                          <Badge variant={todo.done ? "secondary" : "outline"}>
                            {todo.done ? "Done" : "Open"}
                          </Badge>
                          {todo.recurring ? (
                            <Badge variant="accent">Recurring</Badge>
                          ) : null}
                        </div>
                        <p className="text-sm text-muted-foreground">
                          Todo #{todo.id} for user {todo.userId}
                        </p>
                      </div>
                    </div>

                    <div className="flex flex-wrap gap-2 sm:justify-end">
                      <Button
                        variant={todo.recurring ? "secondary" : "outline"}
                        size="sm"
                        onClick={() => void handleToggleRecurring(todo)}
                        disabled={submitting}
                      >
                        <Repeat2 className="mr-2 h-4 w-4" />
                        {todo.recurring ? "Unregister recurring" : "Register recurring"}
                      </Button>
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => void handleDeleteTodo(todo)}
                        disabled={submitting}
                      >
                        <Trash2 className="mr-2 h-4 w-4" />
                        Delete
                      </Button>
                    </div>
                  </article>
                ))}
              </div>
            </CardContent>
          </Card>
        </section>
      </div>
    </main>
  );
}

export default App;
