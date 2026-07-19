export function getErrorMessage(err, fallback) {
  const data = err.response?.data;
  return typeof data === "string" ? data : data?.message || fallback;
}