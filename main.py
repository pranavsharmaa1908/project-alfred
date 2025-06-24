import multiprocessing

from langchain_community.chat_models import ChatLlamaCpp

local_model = "models/gemma-3-1b-it-Q8_0.gguf"

llm = ChatLlamaCpp(
    temperature=0.5,
    model_path=local_model,
    n_ctx=10000,
    n_gpu_layers=8,
    n_batch=300,  # Should be between 1 and n_ctx, consider the amount of VRAM in your GPU.
    max_tokens=512,
    n_threads=multiprocessing.cpu_count() - 1,
    repeat_penalty=1.5,
    top_p=0.5,
    verbose=True,
)

messages = [
    (
        "system",
        "You are a helpful assistant that translates English to hindi. Translate the user sentence.",
    ),
    ("human", "I love programming."),
]

ai_msg = llm.invoke(messages)
ai_msg

print(ai_msg.content)
